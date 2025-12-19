package dev.boni.techhelpdesk.ui.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dev.boni.techhelpdesk.R

/**
 * Verifica si el dispositivo tiene hardware biométrico y si el usuario ha registrado una huella.
 * Retorna TRUE solo si el dispositivo está listo para usarse.
 */
fun checkBiometricAvailability(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)

    // Aceptamos huella (STRONG) o reconocimiento facial simple (WEAK)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

    return when (biometricManager.canAuthenticate(authenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true // ¡Todo listo!
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false // Dispositivo viejo sin sensor
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false // Sensor ocupado o dañado
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false // Tiene sensor, pero el usuario no ha guardado su huella en Android
        else -> false
    }
}

/**
 * Lanza el prompt biométrico. (Esta es la que ya tenías, optimizada)
 */
fun authenticateWithBiometric(
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val activity = context as? FragmentActivity ?: return
    val executor = ContextCompat.getMainExecutor(activity)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(context.getString(R.string.biometric_title))
        .setSubtitle(context.getString(R.string.biometric_subtitle))
        .setAllowedAuthenticators(authenticators)
        .setNegativeButtonText(context.getString(R.string.biometric_cancel))
        .build()

    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    onError(errString.toString())
                }
            }
        }
    )
    biometricPrompt.authenticate(promptInfo)
}