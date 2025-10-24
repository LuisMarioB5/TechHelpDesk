package dev.boni.techhelpdesk.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores semánticos (de estado) que no están
 * incluidos por defecto en MaterialTheme.
 */
data class CustomColors(
    // --- Success (Verde) ---
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,

    // --- Warning (Ámbar/Naranja) ---
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color
)

// --- PALETA PARA TEMA CLARO ---
// Verdes y Ámbares de Tailwind que combinan con tus azules
val LightSuccess = Color(0xFF059669) // Green 600
val LightOnSuccess = Color(0xFFFFFFFF) // Blanco
val LightSuccessContainer = Color(0xFFD1FAE5) // Green 100
val LightOnSuccessContainer = Color(0xFF065F46) // Green 800

val LightWarning = Color(0xFFD97706) // Amber 600
val LightOnWarning = Color(0xFFFFFFFF) // Blanco
val LightWarningContainer = Color(0xFFFEF3C7) // Amber 100
val LightOnWarningContainer = Color(0xFF92400E) // Amber 800


// --- PALETA PARA TEMA OSCURO ---
// Versiones más brillantes para buen contraste en fondos oscuros
val DarkSuccess = Color(0xFF34D399) // Green 400
val DarkOnSuccess = Color(0xFF064E3B) // Green 900
val DarkSuccessContainer = Color(0xFF065F46) // Green 800
val DarkOnSuccessContainer = Color(0xFFD1FAE5) // Green 100

val DarkWarning = Color(0xFFFBBF24) // Amber 400
val DarkOnWarning = Color(0xFF78350F) // Amber 900
val DarkWarningContainer = Color(0xFF92400E) // Amber 800
val DarkOnWarningContainer = Color(0xFFFEF3C7) // Amber 100

// --- Instancias de las data class ---
val LightCustomColors = CustomColors(
    success = LightSuccess,
    onSuccess = LightOnSuccess,
    successContainer = LightSuccessContainer,
    onSuccessContainer = LightOnSuccessContainer,
    warning = LightWarning,
    onWarning = LightOnWarning,
    warningContainer = LightWarningContainer,
    onWarningContainer = LightOnWarningContainer
)

val DarkCustomColors = CustomColors(
    success = DarkSuccess,
    onSuccess = DarkOnSuccess,
    successContainer = DarkSuccessContainer,
    onSuccessContainer = DarkOnSuccessContainer,
    warning = DarkWarning,
    onWarning = DarkOnWarning,
    warningContainer = DarkWarningContainer,
    onWarningContainer = DarkOnWarningContainer
)

/**
 * CompositionLocal para proveer los colores personalizados
 * a toda la app.
 */
val LocalCustomColors = staticCompositionLocalOf {
    // Valores por defecto (Modo Claro)
    LightCustomColors
}

