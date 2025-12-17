package dev.boni.techhelpdesk.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CLIENT,
    val photoUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true
)

enum class UserRole {
    CLIENT,
    TECHNICIAN,
    ADMIN
}