package dev.boni.techhelpdesk.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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
            // 1. Crear el usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Error al crear usuario, Firebase no devolvió un usuario.")

            // 2. Crear el mapa de datos para guardar en Firestore
            val userMap = hashMapOf(
                "uid" to firebaseUser.uid,
                "name" to name,
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )

            // 3. Guardar los datos del usuario en la colección "users" en Firestore
            db.collection("users").document(firebaseUser.uid)
                .set(userMap)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            // Si algo falla (ej. email ya existe, contraseña débil)
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
            // 1. Iniciar sesión con Firebase Authentication
            auth.signInWithEmailAndPassword(email, password).await()

            Result.success(Unit)

        } catch (e: Exception) {
            // Si algo falla (ej. contraseña incorrecta, usuario no existe)
            println("Error en loginUser: ${e.message}")
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
            // 1. Enviar correo de recuperación
            auth.sendPasswordResetEmail(email).await()

            Result.success(Unit)

        } catch (e: Exception) {
            // Si algo falla (ej. email no registrado)
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
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Usuario nulo")

            val userDocRef = db.collection("users").document(firebaseUser.uid)
            val documentSnapshot = userDocRef.get().await()

            if (!documentSnapshot.exists()) {
                val userMap = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "name" to (firebaseUser.displayName ?: "Usuario sin nombre"),
                    "email" to (firebaseUser.email ?: ""),
                    "photoUrl" to (firebaseUser.photoUrl?.toString() ?: ""),
                    "createdAt" to System.currentTimeMillis(),
                    "provider" to (firebaseUser.providerData.getOrNull(1)?.providerId ?: "unknown")
                )

                userDocRef.set(userMap).await()
            } else {
                 userDocRef.update("lastLogin", System.currentTimeMillis())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error en signInWithCredential: ${e.message}")
            Result.failure(e)
        }
    }
}