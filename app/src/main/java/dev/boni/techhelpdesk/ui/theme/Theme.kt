//package dev.boni.techhelpdesk.ui.theme
//
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//
//// 🎨 Esquema de colores modo claro (SOLO COLORES ESTÁNDAR)
//private val LightColorScheme = lightColorScheme(
//    primary = LightPrimary,
//    onPrimary = LightOnPrimary,
//    secondary = LightSecondary,
//    onSecondary = LightOnSecondary,
//    background = LightBackground,
//    onBackground = LightOnBackground,
//    surface = LightSurface,
//    onSurface = LightOnSurface,
//    error = LightError,
//    onError = LightOnError
//)
//
//// 🌙 Esquema de colores modo oscuro (SOLO COLORES ESTÁNDAR)
//private val DarkColorScheme = darkColorScheme(
//    primary = DarkPrimary,
//    onPrimary = DarkOnPrimary,
//    secondary = DarkSecondary,
//    onSecondary = DarkOnSecondary,
//    background = DarkBackground,
//    onBackground = DarkOnBackground,
//    surface = DarkSurface,
//    onSurface = DarkOnSurface,
//    error = DarkError,
//    onError = DarkOnError
//)
//
//// 🧱 Tema principal de la app
//@Composable
//fun TechHelpDeskTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
//) {
//    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
//    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors
//
//    CompositionLocalProvider(LocalCustomColors provides customColors) {
//
//        MaterialTheme(
//            colorScheme = colors,
//            typography = AppTypography,
//            shapes = AppShapes,
//            content = content
//        )
//    }
//}
//

package dev.boni.techhelpdesk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// --- Esquema ESTÁNDAR Modo Claro ---
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
    // No incluyas 'success', 'warning', 'info' aquí
)

// --- Esquema ESTÁNDAR Modo Oscuro ---
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

// --- Tema Principal de la App ---
@Composable
fun TechHelpDeskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 1. Selecciona el esquema de color ESTÁNDAR correcto
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // 2. Selecciona el esquema de color PERSONALIZADO correcto
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    // 3. Provee los colores PERSONALIZADOS al árbol de composición
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        // 4. Aplica el tema de Material 3 con los colores ESTÁNDAR
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography, // Asumiendo que tienes AppTypography
            shapes = AppShapes,         // Asumiendo que tienes AppShapes
            content = content
        )
    }
}