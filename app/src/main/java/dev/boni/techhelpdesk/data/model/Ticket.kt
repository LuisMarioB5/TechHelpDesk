package dev.boni.techhelpdesk.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Ticket(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val priority: String = "media", // baja, media, alta
    val status: String = "abierto", // abierto, en_progreso, resuelto, cerrado
    val userId: String = "", // ID del cliente que creó el ticket
    val createdBy: String = "", // Nombre del cliente
    val assignedToName: String = "Sin asignar",
    val assignedToId: String? = null, // ID del técnico asignado
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val location: String = "",
    val department: String = "",
    val contactMethod: String = "",
    val resolution: String? = null // Resolución del técnico (cuando se cierra)
)

// Estados posibles para filtros
enum class TicketStatus(val value: String) {
    ABIERTO("abierto"),
    EN_PROGRESO("en_progreso"),
    RESUELTO("resuelto"),
    CERRADO("cerrado");

    companion object {
        fun fromString(value: String?): TicketStatus? {
            return values().find { it.value == value }
        }
    }
}

// Prioridades
enum class TicketPriority(val value: String) {
    BAJA("baja"),
    MEDIA("media"),
    ALTA("alta");

    companion object {
        fun fromString(value: String?): TicketPriority? {
            return values().find { it.value == value }
        }
    }
}