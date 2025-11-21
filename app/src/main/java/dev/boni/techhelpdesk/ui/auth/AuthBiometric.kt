package dev.boni.techhelpdesk.ui.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


    private var canAuthenticate = false
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun authenticateWithBiometric(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Obtenemos la actividad. Necesitamos castearla a FragmentActivity
        val activity = context as? FragmentActivity ?: return

        val executor = ContextCompat.getMainExecutor(activity)

        // 2. Configurar el prompt (Ahora sí reconocerá PromptInfo)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Inicio de sesión biométrico")
            .setSubtitle("Usa tu huella o rostro")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancelar")
            .build()

        // 3. Configurar los callbacks
        if(canAuthenticate) {
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError(errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // La huella no coincide
                    }
                }
            )

            // 4. Lanzar la autenticación
            biometricPrompt.authenticate(promptInfo)
        }

    }
