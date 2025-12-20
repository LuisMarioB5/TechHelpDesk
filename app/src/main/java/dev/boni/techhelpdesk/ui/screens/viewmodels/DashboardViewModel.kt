package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.model.TicketStatus
import dev.boni.techhelpdesk.data.model.UserRole
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userName: String = "",
    val openCount: Int = 0,
    val inProgressCount: Int = 0,
    val closedCount: Int = 0,
    val isLoading: Boolean = true,
    val userRole: UserRole = UserRole.CLIENT,
    val userPhotoUrl: String? = ""
)

class DashboardViewModel : ViewModel() {

    private val authRepo = AuthRepository()
    private val ticketRepo = TicketRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepo.getCurrentUser().getOrNull()
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val myName = authRepo.getCachedDisplayName() ?: currentUser.name
            val firstName = myName.split(" ").firstOrNull() ?: "Usuario"

            ticketRepo.getTicketsFlow().collect { allTickets ->

                val myTickets = allTickets.filter { ticket ->
                    ticket.createdBy == myName || ticket.assignedToName == myName
                }

                val open = myTickets.count {
                    it.status.equals(TicketStatus.ABIERTO.name, ignoreCase = true)
                }

                val inProgress = myTickets.count {
                    it.status.equals(TicketStatus.EN_PROGRESO.name, ignoreCase = true) ||
                            it.status.equals("EN PROGRESO", ignoreCase = true)
                }

                val closed = myTickets.count {
                    it.status.equals(TicketStatus.CERRADO.name, ignoreCase = true) ||
                            it.status.equals(TicketStatus.RESUELTO.name, ignoreCase = true)
                }

                _uiState.update {
                    it.copy(
                        userName = firstName,
                        openCount = open,
                        inProgressCount = inProgress,
                        closedCount = closed,
                        isLoading = false,
                        userRole = currentUser.role,
                        userPhotoUrl = currentUser.photoUrl
                    )
                }
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}