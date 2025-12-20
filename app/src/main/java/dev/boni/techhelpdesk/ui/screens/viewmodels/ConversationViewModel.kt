package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.data.repository.TicketRepository
import dev.boni.techhelpdesk.ui.screens.conversation.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ConversationViewModel : ViewModel() {

    private val ticketRepo = TicketRepository()
    private val authRepo = AuthRepository()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadConversations() {
        _isLoading.value = true
        viewModelScope.launch {
            // Obtenemos el nombre del usuario actual (caché o vacío)
            val myName = authRepo.getCachedDisplayName() ?: ""

            // Recolectamos el Flow. Aquí ESPECIFICAMOS que 'tickets' es una lista de Tickets
            ticketRepo.getTicketsFlow().collect { tickets ->

                // 1. FILTRADO
                // Filtramos solo los tickets que me pertenecen (creado por mí o asignado a mí)
                val myTickets = tickets.filter { ticket ->
                    ticket.createdBy == myName || ticket.assignedToName == myName
                }

                // 2. MAPEO (Transformación a UI)
                val mappedConversations = myTickets.map { ticket ->

                    // Lógica para saber quién es la "otra persona"
                    val otherPersonName = if (ticket.createdBy == myName) {
                        // Si yo lo creé, hablo con el técnico (o dice Soporte si nadie lo tiene)
                        if (ticket.assignedToName.isNotEmpty()) ticket.assignedToName else "Soporte General"
                    } else {
                        // Si yo soy el técnico, hablo con quien lo creó
                        ticket.createdBy
                    }

                    // Formatear fecha
                    val dateStr = try {
                        ticket.updatedAt.toDate().let { date ->
                            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(date)
                        }
                    } catch (e: Exception) {
                        "..."
                    }

                    // Mapeo final al objeto Conversation
                    Conversation(
                        id = ticket.id,
                        technicianName = otherPersonName,
                        technicianAvatar = otherPersonName.firstOrNull()?.uppercase() ?: "?",
                        lastMessage = ticket.title,
                        timestamp = dateStr,
                        unreadCount = if (ticket.status == "ABIERTO" || ticket.status == "EN_PROGRESO") 1 else 0,
                    )
                }

                _conversations.value = mappedConversations
                _isLoading.value = false
            }
        }
    }
}