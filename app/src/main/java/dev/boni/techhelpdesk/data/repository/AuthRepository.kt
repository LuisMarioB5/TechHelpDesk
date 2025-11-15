package dev.boni.techhelpdesk.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Un repositorio para manejar todas las tareas de autenticación
 * y base de datos de usuarios con Firebase.
 */
class AuthRepository {

    // Obtener las instancias de Firebase Auth y Firestore
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

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
}