package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel encargado de gestionar el estado y la lógica de negocio del Dashboard.
 */
open class DashboardViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    /**
     * Estado del nombre de usuario.
     * Inicializamos llamando al repositorio (caché) para que sea instantáneo.
     */
    private val _userName = MutableStateFlow(authRepo.getCachedDisplayName() ?: "Usuario")
    open val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        refreshUserName()
    }

    /**
     * Refresca el nombre de usuario desde el repositorio de manera asíncrona
     * para asegurar consistencia en caso de cambios remotos.
     */
    private fun refreshUserName() {
        viewModelScope.launch {
            val result = authRepo.getCurrentUserNameFromFirestore()
            if (result.isSuccess) {
                _userName.value = result.getOrNull() ?: "Usuario"
            }
        }
    }
}