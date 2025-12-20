package dev.boni.techhelpdesk.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import dev.boni.techhelpdesk.data.model.Ticket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.toObjects
import dev.boni.techhelpdesk.data.model.TicketStatus

class TicketRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Crea un nuevo ticket en la colección 'tickets'.
     */
    suspend fun createTicket(
        title: String,
        description: String,
        category: String,
        priority: String,
        location: String,
        department: String,
        contactMethod: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw IllegalStateException("No hay usuario logueado")

            val userName = user.displayName ?: "Usuario"

            val newTicketRef = db.collection("tickets").document()

            val ticket = Ticket(
                id = newTicketRef.id,
                title = title,
                description = description,
                category = category,
                priority = priority,
                userId = user.uid,
                createdBy = userName,
                location = location,
                department = department,
                contactMethod = contactMethod,
                status = "abierto"
            )

            newTicketRef.set(ticket).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los tickets del usuario actual, ordenados por fecha.
     */
    suspend fun getUserTickets(statusFilter: String? = null): Result<List<Ticket>> {
        return try {
            val user = auth.currentUser ?: throw IllegalStateException("No hay usuario")

            var query = db.collection("tickets")
                .whereEqualTo("userId", user.uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (statusFilter != null) {
                query = query.whereEqualTo("status", statusFilter)
            }

            val snapshot = query.get().await()
            val tickets = snapshot.toObjects<Ticket>()

            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTicketsByUserId(userId: String): Result<List<Ticket>> {
        return try {
            val snapshot = db.collection("tickets")
                .whereEqualTo("userId", userId)
                 .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val tickets = snapshot.toObjects(Ticket::class.java)
            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un ticket por ID
     */
    suspend fun getTicketById(ticketId: String): Result<Ticket> {
        return try {
            val doc = db.collection("tickets")
                .document(ticketId)
                .get()
                .await()

            if (doc.exists()) {
                val ticket = doc.toObject(Ticket::class.java)
                    ?: return Result.failure(Exception("Error al convertir ticket"))

                Result.success(ticket.copy(id = doc.id))
            } else {
                Result.failure(Exception("Ticket no encontrado"))
            }
        } catch (e: Exception) {
            println("Error en getTicketById: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Actualiza el estado de un ticket
     */
    suspend fun updateTicketStatus(ticketId: String, newStatus: TicketStatus): Result<Unit> {
        return try {
            db.collection("tickets")
                .document(ticketId)
                .update("status", newStatus.name)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error en updateTicketStatus: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene TODOS los tickets (Para el Dashboard del Técnico).
     */
    suspend fun getAllTickets(): Result<List<Ticket>> {
        return try {
            // Traemos todos, ordenados por fecha
            val snapshot = db.collection("tickets")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val tickets = snapshot.toObjects<Ticket>()
            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTicketFields(ticketId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            db.collection("tickets")
                .document(ticketId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el nombre del usuario en TODOS sus tickets relacionados.
     * (Tanto los que creó como los que tiene asignados).
     */
    suspend fun updateTicketsUserDisplayName(userId: String, newName: String) {
        try {
            val batch = db.batch()
            var operationCount = 0

            val createdSnapshot = db.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            for (doc in createdSnapshot.documents) {
                batch.update(doc.reference, "createdBy", newName)
                operationCount++
            }

            val assignedSnapshot = db.collection("tickets")
                .whereEqualTo("assignedToId", userId)
                .get()
                .await()

            for (doc in assignedSnapshot.documents) {
                batch.update(doc.reference, "assignedToName", newName)
                operationCount++
            }

            if (operationCount > 0) {
                batch.commit().await()
            }
        } catch (e: Exception) {
            println("Error actualizando nombre en tickets: ${e.message}")
        }
    }

    /**
     * Escucha los mensajes de un ticket en tiempo real.
     * Retorna un Flow (flujo de datos) que se actualiza solo.
     */
    fun getTicketMessages(ticketId: String): kotlinx.coroutines.flow.Flow<List<dev.boni.techhelpdesk.data.model.ChatMessage>> = callbackFlow {
        val subscription = db.collection("tickets").document(ticketId)
            .collection("messages")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(dev.boni.techhelpdesk.data.model.ChatMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    /**
     * Envía un mensaje a la subcolección del ticket.
     */
    suspend fun sendMessage(ticketId: String, message: dev.boni.techhelpdesk.data.model.ChatMessage): Result<Unit> {
        return try {
            db.collection("tickets").document(ticketId)
                .collection("messages")
                .add(message)
                .await()

            val updates = mapOf(
                "lastMessage" to message.text,
                "lastMessageTimestamp" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )

            db.collection("tickets").document(ticketId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene TODOS los tickets en tiempo real.
     * Retorna un Flow<List<Ticket>>.
     */
    fun getTicketsFlow(): kotlinx.coroutines.flow.Flow<List<dev.boni.techhelpdesk.data.model.Ticket>> = kotlinx.coroutines.flow.callbackFlow {
        // Ordenamos por fecha de actualización para que los chats recientes salgan arriba
        val subscription = db.collection("tickets")
            .orderBy("updatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val tickets = snapshot?.toObjects(dev.boni.techhelpdesk.data.model.Ticket::class.java) ?: emptyList()
                trySend(tickets)
            }

        awaitClose { subscription.remove() }
    }
}
