package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import dev.boni.techhelpdesk.data.model.Ticket
import dev.boni.techhelpdesk.data.model.TicketStatus
import dev.boni.techhelpdesk.data.model.UserRole
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados de carga para la UI
 */
sealed class TicketUiState {
    object Initial : TicketUiState()
    object Loading : TicketUiState()
    data class Success(val tickets: List<Ticket>) : TicketUiState()
    data class Error(val message: String) : TicketUiState()
}

/**
 * ViewModel para manejar tickets
 */
class TicketViewModel : ViewModel() {

    private val repository = TicketRepository()
    private val authRepo = AuthRepository()


    private val _uiState = MutableStateFlow<TicketUiState>(TicketUiState.Initial)
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _currentTicket = MutableStateFlow<Ticket?>(null)
    val currentTicket: StateFlow<Ticket?> = _currentTicket.asStateFlow()

    private val _isTechnician = MutableStateFlow(false)
    val isTechnician: StateFlow<Boolean> = _isTechnician.asStateFlow()

    private val _messages = MutableStateFlow<List<dev.boni.techhelpdesk.data.model.ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    /**
     * Lógica maestra: Chequea rol y carga los tickets correspondientes.
     * Úsala en TicketsScreen (Lista).
     */
    fun checkRoleAndLoadTickets() {
        viewModelScope.launch {
            _uiState.value = TicketUiState.Loading

            val userResult = authRepo.getCurrentUser()
            val user = userResult.getOrNull()
            val isTech = user?.role == UserRole.TECHNICIAN || user?.role == UserRole.ADMIN

            _isTechnician.value = isTech

            val result = if (isTech) {
                repository.getAllTickets()
            } else {
                repository.getUserTickets()
            }

            _uiState.value = if (result.isSuccess) {
                TicketUiState.Success(result.getOrNull() ?: emptyList())
            } else {
                TicketUiState.Error(result.exceptionOrNull()?.message ?: "Error cargando tickets")
            }
        }
    }

    /**
     * Solo chequea el rol (sin cargar lista).
     * Úsala en TicketDetailScreen (Detalle) para saber si mostrar botones.
     */
    fun checkUserRole() {
        viewModelScope.launch {
            val user = authRepo.getCurrentUser().getOrNull()
            _isTechnician.value = user?.role == UserRole.TECHNICIAN || user?.role == UserRole.ADMIN
        }
    }

    /**
     * Carga los tickets del usuario
     */
    fun loadUserTickets() {
        viewModelScope.launch {
            _uiState.value = TicketUiState.Loading

            val result = repository.getUserTickets()

            _uiState.value = if (result.isSuccess) {
                TicketUiState.Success(result.getOrNull() ?: emptyList())
            } else {
                TicketUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Carga un ticket específico
     */
    fun loadTicket(ticketId: String) {
        viewModelScope.launch {
            val result = repository.getTicketById(ticketId)
            if (result.isSuccess) {
                _currentTicket.value = result.getOrNull()
            }
        }
    }

    /**
     * Crea un nuevo ticket
     */
    suspend fun createTicket(
        title: String,
        description: String,
        category: String,
        priority: String,
        location: String = "",
        department: String = "",
        contactMethod: String = ""
    ): Result<Unit> {
        return repository.createTicket(
            title, description, category, priority,
            location, department, contactMethod
        )
    }

    /**
     *  Carga TODOS los tickets (Modo Técnico)
     */
    fun loadAllTickets() {
        viewModelScope.launch {
            _uiState.value = TicketUiState.Loading
            val result = repository.getAllTickets() // <--- Llamamos a la nueva función

            _uiState.value = if (result.isSuccess) {
                TicketUiState.Success(result.getOrNull() ?: emptyList())
            } else {
                TicketUiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tickets")
            }
        }
    }

    /**
     *  Cambia el estado del ticket actual
     */
    fun updateTicketStatus(newStatus: TicketStatus) {
        val ticket = _currentTicket.value ?: return

        viewModelScope.launch {
            val currentUserResult = authRepo.getCurrentUser().getOrNull()
            val technicianName = currentUserResult?.name ?: "Técnico"
            val technicianId = currentUserResult?.id ?: ""

            val updateData = mutableMapOf<String, Any>(
                "status" to newStatus.name,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (newStatus == TicketStatus.EN_PROGRESO) {
                updateData["assignedToId"] = technicianId
                updateData["assignedToName"] = technicianName
            }

            val result = repository.updateTicketFields(ticket.id, updateData)

            if (result.isSuccess) {
                _currentTicket.value = ticket.copy(
                    status = newStatus.name,
                    updatedAt = Timestamp.now(),
                    assignedToName = if (newStatus == TicketStatus.EN_PROGRESO) technicianName else ticket.assignedToName,
                    assignedToId = if (newStatus == TicketStatus.EN_PROGRESO) technicianId else ticket.assignedToId
                )

                checkRoleAndLoadTickets()
            } else {
                println("Error actualizando status: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun loadMessages(ticketId: String) {
        viewModelScope.launch {
            repository.getTicketMessages(ticketId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val ticket = _currentTicket.value ?: return

        viewModelScope.launch {
            val userResult = authRepo.getCurrentUser()
            val user = userResult.getOrNull() ?: return@launch

            val newMessage = dev.boni.techhelpdesk.data.model.ChatMessage(
                senderId = user.id,
                senderName = user.name,
                text = text,
                timestamp = null
            )

            repository.sendMessage(ticket.id, newMessage)
        }
    }
}