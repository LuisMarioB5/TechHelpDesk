//package dev.boni.techhelpdesk.ui.theme
//
//import androidx.compose.runtime.staticCompositionLocalOf
//import androidx.compose.ui.graphics.Color
//
///**
// * Paleta de colores semánticos (de estado) que no están
// * incluidos por defecto en MaterialTheme.
// */
//data class CustomColors(
//    // --- Success (Verde) ---
//    val success: Color,
//    val onSuccess: Color,
//    val successContainer: Color,
//    val onSuccessContainer: Color,
//
//    // --- Warning (Ámbar/Naranja) ---
//    val warning: Color,
//    val onWarning: Color,
//    val warningContainer: Color,
//    val onWarningContainer: Color
//)
//
//// --- Instancias de las data class ---
//val LightCustomColors = CustomColors(
//    success = LightSuccess,
//    onSuccess = LightOnSuccess,
//    successContainer = LightSuccessContainer,
//    onSuccessContainer = LightOnSuccessContainer,
//    warning = LightWarning,
//    onWarning = LightOnWarning,
//    warningContainer = LightWarningContainer,
//    onWarningContainer = LightOnWarningContainer
//)
//
//val DarkCustomColors = CustomColors(
//    success = DarkSuccess,
//    onSuccess = DarkOnSuccess,
//    successContainer = DarkSuccessContainer,
//    onSuccessContainer = DarkOnSuccessContainer,
//    warning = DarkWarning,
//    onWarning = DarkOnWarning,
//    warningContainer = DarkWarningContainer,
//    onWarningContainer = DarkOnWarningContainer
//)
//
///**
// * CompositionLocal para proveer los colores personalizados
// * a toda la app.
// */
//val LocalCustomColors = staticCompositionLocalOf {
//    // Valores por defecto (Modo Claro)
//    LightCustomColors
//}


package dev.boni.techhelpdesk.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores semánticos personalizados que no forman parte del estándar de Material 3.
 */
data class CustomColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,

    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,

    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,

    // Prioridades
    val priorityCritical: Color,
    val priorityHigh: Color,
    val priorityMedium: Color,
    val priorityLow: Color
)

// --- Instancia CLARA de Colores Personalizados ---
val LightCustomColors = CustomColors(
    success = LightSuccess,
    onSuccess = LightOnSuccess,
    successContainer = LightSuccessContainer,
    onSuccessContainer = LightOnSuccessContainer,
    warning = LightWarning,
    onWarning = LightOnWarning,
    warningContainer = LightWarningContainer,
    onWarningContainer = LightOnWarningContainer,
    info = LightInfo,
    onInfo = LightOnInfo,
    infoContainer = LightInfoContainer,
    onInfoContainer = LightOnInfoContainer,
    priorityCritical = LightPriorityCritical,
    priorityHigh = LightPriorityHigh,
    priorityMedium = LightPriorityMedium,
    priorityLow = LightPriorityLow
)

// --- Instancia OSCURA de Colores Personalizados ---
val DarkCustomColors = CustomColors(
    success = DarkSuccess,
    onSuccess = DarkOnSuccess,
    successContainer = DarkSuccessContainer,
    onSuccessContainer = DarkOnSuccessContainer,
    warning = DarkWarning,
    onWarning = DarkOnWarning,
    warningContainer = DarkWarningContainer,
    onWarningContainer = DarkOnWarningContainer,
    info = DarkInfo,
    onInfo = DarkOnInfo,
    infoContainer = DarkInfoContainer,
    onInfoContainer = DarkOnInfoContainer,
    priorityCritical = DarkPriorityCritical,
    priorityHigh = DarkPriorityHigh,
    priorityMedium = DarkPriorityMedium,
    priorityLow = DarkPriorityLow
)

// --- Proveedor de CompositionLocal ---
// Esto permite que LocalCustomColors.current funcione
val LocalCustomColors = compositionLocalOf { LightCustomColors }