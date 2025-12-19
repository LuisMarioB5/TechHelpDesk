package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.model.Ticket
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

    private val _uiState = MutableStateFlow<TicketUiState>(TicketUiState.Initial)
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _currentTicket = MutableStateFlow<Ticket?>(null)
    val currentTicket: StateFlow<Ticket?> = _currentTicket.asStateFlow()

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
}