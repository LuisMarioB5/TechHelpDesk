package dev.boni.techhelpdesk.ui.screens.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.data.local.SettingsPreferences
import dev.boni.techhelpdesk.data.model.UserRole
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val roleResId: Int = R.string.role_client,
    val isLoading: Boolean = true,
    val isBiometricEnabled: Boolean = true,
    val areNotificationsEnabled: Boolean = false
)

class ProfileViewModel() : ViewModel() {
    private val authRepo = AuthRepository()
    private val ticketRepo = TicketRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfileInfo(context: Context) {
        loadUserProfile()
        loadSettings(context)
    }

    private fun loadSettings(context: Context) {
        val prefs = SettingsPreferences(context)
        _uiState.update {
            it.copy(
                isBiometricEnabled = prefs.isBiometricEnabled(),
                areNotificationsEnabled = prefs.areNotificationsEnabled()
            )
        }
    }

    fun toggleBiometric(context: Context, enabled: Boolean) {
        val prefs = SettingsPreferences(context)
        prefs.setBiometricEnabled(enabled)
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun toggleNotifications(context: Context, enabled: Boolean) {
        val prefs = SettingsPreferences(context)
        prefs.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(areNotificationsEnabled = enabled) }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val result = authRepo.getCurrentUser()

            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _uiState.update {
                    it.copy(
                        name = user.name,
                        email = user.email,
                        phone = user.phone,
                        roleResId = getRoleResourceId(user.role),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Guarda los cambios del perfil (Nombre y Teléfono).
     */
    fun updateUserProfile(newName: String, newPhone: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepo.updateUserProfile(newName, newPhone)
            if (result.isSuccess) {
                val userId = authRepo.getCurrentUser().getOrNull()?.id

                if (userId != null) {
                    ticketRepo.updateTicketsUserDisplayName(userId, newName)
                }

                _uiState.update {
                    it.copy(name = newName, isLoading = false)
                }
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Envía un correo de cambio de contraseña al email actual.
     * Es la forma más segura sin pedir la contraseña antigua.
     */
    fun triggerPasswordChange(onEmailSent: () -> Unit) {
        val currentEmail = _uiState.value.email
        if (currentEmail.isNotBlank()) {
            viewModelScope.launch {
                authRepo.recoverPassword(currentEmail)
                onEmailSent()
            }
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

    private fun getRoleResourceId(role: UserRole): Int {
        return when (role) {
            UserRole.ADMIN -> R.string.role_admin
            UserRole.CLIENT -> R.string.role_client
            UserRole.TECHNICIAN -> R.string.role_technician
        }
    }
}