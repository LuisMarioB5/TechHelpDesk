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
            val userId = currentUser?.id ?: ""
            val name = currentUser?.name?.split(" ")?.firstOrNull() ?: "Usuario"

            if (userId.isNotEmpty()) {
                val result = ticketRepo.getTicketsByUserId(userId)

                result.onSuccess { tickets ->
                    val open = tickets.count {
                        it.status.equals(TicketStatus.ABIERTO.name, ignoreCase = true)
                    }

                    val inProgress = tickets.count {
                        it.status.equals(TicketStatus.EN_PROGRESO.name, ignoreCase = true)
                    }

                    val closed = tickets.count {
                        it.status.equals(TicketStatus.CERRADO.name, ignoreCase = true)
                    }

                    _uiState.update {
                        it.copy(
                            userName = name,
                            openCount = open,
                            inProgressCount = inProgress,
                            closedCount = closed,
                            isLoading = false,
                            userRole = currentUser?.role ?: UserRole.CLIENT,
                            userPhotoUrl = currentUser?.photoUrl
                        )
                    }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false, userName = name) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}