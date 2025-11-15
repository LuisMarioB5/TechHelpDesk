package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import dev.boni.techhelpdesk.data.repository.AuthRepository

/**
 * ViewModel para la ProfileScreen.
 * Se encarga de manejar la lógica de la pantalla de perfil.
 */
class ProfileViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    /**
     * Llama al repositorio para cerrar la sesión del usuario.
     */
    fun onConfirmLogout() {
        authRepo.signOut()
    }
}