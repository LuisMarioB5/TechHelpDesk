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