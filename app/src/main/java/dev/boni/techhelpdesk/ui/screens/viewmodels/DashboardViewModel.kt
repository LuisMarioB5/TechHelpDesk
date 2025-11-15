package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la DashboardScreen.
 * Se encarga de obtener y mantener el estado de la UI del Dashboard.
 */
open class DashboardViewModel : ViewModel() {

    // Instancia del repositorio
    private val authRepo = AuthRepository()

    // --- Estado del Nombre de Usuario ---
    // Privado y mutable (solo el ViewModel puede cambiarlo)
    private val _userName = MutableStateFlow("Usuario") // Valor por defecto
    // Público e inmutable (la UI solo puede leerlo)
    open val userName: StateFlow<String> = _userName.asStateFlow()

    // Bloque de inicialización: se llama en cuanto el ViewModel se crea
    init {
        loadUserName()
    }

    /**
     * Llama al repositorio para obtener el nombre del usuario
     * y actualiza el estado _userName.
     */
    private fun loadUserName() {
        // viewModelScope se encarga de cancelar esto si el ViewModel se destruye
        viewModelScope.launch {
            val result = authRepo.getCurrentUserName()
            if (result.isSuccess) {
                // Si se obtiene el nombre, actualiza el valor del StateFlow
                _userName.value = result.getOrNull() ?: "Usuario"
            } else {
                // Si falla (ej. no logueado), se queda como "Usuario"
                _userName.value = "Invitado" // O puedes manejar el error
            }
        }
    }
}
