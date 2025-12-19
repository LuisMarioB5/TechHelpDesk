package dev.boni.techhelpdesk.data.repository

import android.content.Context
import android.util.Log
import dev.boni.techhelpdesk.data.model.User
import dev.boni.techhelpdesk.data.model.UserRole
import com.google.firebase.Timestamp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import android.widget.Toast

/**
 * Un repositorio para manejar todas las tareas de autenticación
 * y base de datos de usuarios con Firebase.
 */
class AuthRepository {

    // Obtener las instancias de Firebase Auth y Firestore
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    /**
     * Cierra sesión COMPLETAMENTE: Firebase + Google.
     * Es una función 'suspend' para que espere a que Google termine antes de retornar.
     */
    suspend fun signOut(context: Context) {
        try {
            // 1. Cerrar sesión en Firebase (esto es síncrono e instantáneo)
            auth.signOut()

            // 2. Obtener el cliente de Google
            // No necesitamos configurar el RequestToken aquí, solo con DEFAULT_SIGN_IN basta para el signOut
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            // 3. Cerrar sesión en Google y ESPERAR (.await())
            // Esto limpia la caché de la cuenta seleccionada
            googleSignInClient.signOut().await()

        } catch (e: Exception) {
            val msg = "Error al cerrar sesión de Google: ${e.message}"

            // Si falla Google (ej. no había sesión de Google), no pasa nada, seguimos
            println(msg)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        }
    }

    /**
     * Obtiene el nombre del usuario directamente de la caché local de Auth.
     * Es SÍNCRONO (instantáneo), ideal para evitar parpadeos en la UI.
     */
    fun getCachedDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    /**
     * Verifica si existe una sesión activa de Firebase.
     * @return true si hay un usuario logueado, false si no.
    */
    fun isSessionActive(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Obtiene el nombre del usuario actualmente logueado desde Firestore.
     *
     * @return Result.success(String) con el nombre del usuario.
     * @return Result.failure(Exception) si el usuario no está logueado o no se encuentra el nombre.
     */
    suspend fun getCurrentUserNameFromFirestore(): Result<String> {
        return try {
            val currentUser = auth.currentUser
                ?: throw IllegalStateException("Nadie ha iniciado sesión")

            val userDocument = db.collection("users").document(currentUser.uid).get().await()
            val userName = userDocument.getString("name")
                ?: throw IllegalStateException("Sin campo 'name'")

            Result.success(userName)
        } catch (e: Exception) {
            println("Error en getCurrentUserName: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     * Si tiene éxito, también guarda el nombre y email en Firestore.
     *
     * @param name El nombre completo del usuario.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña (debe tener mín. 6 caracteres).
     * @return Result.success(Unit) si todo fue exitoso.
     * @return Result.failure(Exception) si algo salió mal.
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Error al crear usuario")

            // Actualizar displayName en Firebase Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Crear usuario en Firestore con el modelo User
            val user = User(
                name = name,
                email = email,
                role = UserRole.CLIENT,
                photoUrl = null,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                lastLogin = Timestamp.now(),
                isActive = true
            )

            db.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error en registerUser: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión de un usuario con email y contraseña.
     *
     * @param email El correo del usuario.
     * @param password La contraseña del usuario.
     * @return Result.success(Unit) si el inicio de sesión es exitoso.
     * @return Result.failure(Exception) si algo salió mal.
     */
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            // 1. Iniciar sesión en el sistema de Auth (La puerta)
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // 2. Si entramos, actualizamos Firestore (El libro de registro)
                val currentTime = Timestamp.now()

                // Solo hacemos un 'update' porque asumimos que si tiene email/pass,
                // ya se registró antes y su documento existe.
                db.collection("users").document(firebaseUser.uid)
                    .update(
                        mapOf(
                            "lastLogin" to currentTime,
                            "updatedAt" to currentTime
                        )
                    ).await() // ¡Importante esperar a que se guarde!

                Log.d("AuthDebug", "✅ Login Email/Pass exitoso y fecha actualizada.")
            }

            Result.success(Unit)

        } catch (e: Exception) {
            // Si falla el login o si el usuario no existe en la base de datos
            Log.e("AuthDebug", "❌ Error en loginUser", e)
            Result.failure(e)
        }
    }

    /**
     * Envía un correo de recuperación de contraseña a un email.
     *
     * @param email El correo al que se enviará el enlace.
     * @return Result.success(Unit) si el correo se envía.
     * @return Result.failure(Exception) si algo salió mal.
     */
    suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()

            Result.success(Unit)

        } catch (e: Exception) {
            println("Error en recoverPassword: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Esta función es el CORAZÓN del Social Login.
     * Recibe la credencial (sea de Google, Apple o Microsoft), hace login en Firebase
     * y si el usuario es nuevo, lo registra en Firestore.
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<Unit> {
        val tag = "AuthDebug"

        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User null")

            val userDocRef = db.collection("users").document(firebaseUser.uid)
            val documentSnapshot = userDocRef.get().await()

            val currentTime = Timestamp.now()

            if (!documentSnapshot.exists()) {
                Log.i(tag, "📝 Creando usuario nuevo...")
                val newUser = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "Usuario sin nombre",
                    email = firebaseUser.email ?: "",
                    role = UserRole.CLIENT,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = currentTime,
                    updatedAt = currentTime,
                    lastLogin = currentTime,
                    isActive = true
                )
                userDocRef.set(newUser).await()
            } else {
                Log.d(tag, "🔄 Usuario recurrente. Actualizando lastLogin...")

                userDocRef.update(
                    mapOf(
                        "lastLogin" to currentTime,
                        "updatedAt" to currentTime
                    )
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el objeto User completo desde Firestore.
     * Necesario para saber el ROL, la foto, etc.
     */
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw IllegalStateException("No hay sesión activa")

            val document = db.collection("users").document(uid).get().await()

            val user = document.toObject(User::class.java)
                ?: throw IllegalStateException("El usuario existe en Auth pero no en Firestore")

            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error obteniendo datos del usuario", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza el nombre y teléfono del usuario actual.
     * Actualiza tanto Firestore como el perfil de Auth.
     */
    suspend fun updateUserProfile(name: String, phone: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw IllegalStateException("No hay usuario logueado")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates).await()

            val updates = mapOf(
                "name" to name,
                "phone" to phone,
                "updatedAt" to Timestamp.now()
            )

            db.collection("users").document(user.uid)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}