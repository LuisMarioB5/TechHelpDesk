package dev.boni.techhelpdesk.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.boni.techhelpdesk.data.model.User
import dev.boni.techhelpdesk.data.model.UserRole
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Obtiene el usuario actual desde Firestore
     */
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val currentUserId = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay usuario autenticado"))

            val doc = db.collection("users")
                .document(currentUserId)
                .get()
                .await()

            if (doc.exists()) {
                val user = doc.toObject(User::class.java)
                    ?: return Result.failure(Exception("Error al convertir documento"))
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado en Firestore"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un usuario por ID
     */
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val doc = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                val user = doc.toObject(User::class.java)
                    ?: return Result.failure(Exception("Error al convertir documento"))
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el perfil del usuario actual
     */
    suspend fun updateUserProfile(updates: Map<String, Any>): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay usuario autenticado"))

            db.collection("users")
                .document(currentUserId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los técnicos (para asignar tickets)
     */
    suspend fun getAllTechnicians(): Result<List<User>> {
        return try {
            val snapshot = db.collection("users")
                .whereEqualTo("role", UserRole.TECHNICIAN.name)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val technicians = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }

            Result.success(technicians)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}