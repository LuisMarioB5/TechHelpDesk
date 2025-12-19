package dev.boni.techhelpdesk.ui.screens.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "Cargando...",
    val email: String = "",
    val role: String = "",
    val isLoading: Boolean = true
)

class ProfileViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Asumo que tu AuthRepository tiene una función para obtener el usuario actual de Firestore.
            // Si no la tienes, avísame, pero generalmente devuelve tu data class 'User'.
            val result = authRepo.getCurrentUser()

            result.onSuccess { user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        name = user.name,
                        email = user.email,
                        // Convertimos el Enum a un String bonito (Ej: CLIENT -> Cliente)
                        role = formatRoleName(user.role),
                        isLoading = false
                    )
                }
            }.onFailure {
                // Manejo de error si no se pudo cargar el usuario
                _uiState.update { it.copy(name = "Usuario", role = "Invitado", isLoading = false) }
            }
        }
    }

    private fun formatRoleName(role: UserRole): String {
        return when (role) {
            UserRole.ADMIN -> "Administrador"
            UserRole.TECHNICIAN -> "Técnico"
            UserRole.CLIENT -> "Cliente"
            else -> role.name
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