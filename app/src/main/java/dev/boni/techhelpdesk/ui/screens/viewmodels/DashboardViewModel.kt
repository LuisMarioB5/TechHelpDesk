package dev.boni.techhelpdesk.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                val firestoreName = result.getOrNull() ?: "Usuario"

                _userName.value = firestoreName

                val authUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                if (authUser != null && authUser.displayName != firestoreName) {

                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(firestoreName)
                        .build()

                    try {
                        authUser.updateProfile(profileUpdates).await()
                    } catch (e: Exception) {
                        println("Error al actualizar el nombre de usuario en Firebase Auth: $e")
                    }
                }
            }
        }
    }
}