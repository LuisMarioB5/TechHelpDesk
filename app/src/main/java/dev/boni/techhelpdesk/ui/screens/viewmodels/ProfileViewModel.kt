package dev.boni.techhelpdesk.ui.screens.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.data.model.UserRole
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "Cargando...",
    val email: String = "",
    val roleResId: Int = R.string.role_client,
    val isLoading: Boolean = true
)

class ProfileViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    // Estado observable (Backing property)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val result = authRepo.getCurrentUser()

            result.onSuccess { user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        name = user.name,
                        email = user.email,
                        roleResId = getRoleResourceId(user.role),
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun getRoleResourceId(role: UserRole): Int {
        return when (role) {
            UserRole.ADMIN -> R.string.role_admin
            UserRole.CLIENT -> R.string.role_client
            UserRole.TECHNICIAN -> R.string.role_technician
        }
    }

    /**
     * Llama al repositorio para cerrar la sesión del usuario.
     */
    fun onConfirmLogout(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepo.signOut(context)
            onSuccess()
        }
    }
}