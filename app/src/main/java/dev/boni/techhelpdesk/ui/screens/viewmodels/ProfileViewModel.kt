package dev.boni.techhelpdesk.ui.screens.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    /**
     * Llama al repositorio para cerrar la sesión del usuario.
     * Recibe el contexto necesario para limpiar la sesión de Google.
     * Recibe una función 'onSuccess' para avisar a la UI cuando termine.
     */
    fun onConfirmLogout(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepo.signOut(context)

            onSuccess()
        }
    }
}