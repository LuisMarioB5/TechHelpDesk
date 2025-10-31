//package dev.boni.techhelpdesk.ui.theme
//
//import androidx.compose.ui.graphics.Color
//
// ----- Light Theme Colors -----
//
// Colores Principales
//val LightPrimary = Color(0xFF1E40AF)     // Azul intenso
//val LightOnPrimary = Color(0xFFFFFFFF)    // Blanco
//val LightSecondary = Color(0xFF64748B)    // Gris azulado
//val LightOnSecondary = Color(0xFFFFFFFF)  // Blanco
//val LightBackground = Color(0xFFF1F5F9)   // Gris muy claro
//val LightOnBackground = Color(0xFF0F172A) // Texto oscuro
//val LightSurface = Color(0xFFFFFFFF)      // Fondo de tarjetas, etc.
//val LightOnSurface = Color(0xFF64748B)    // Texto sobre superficie
//val LightError = Color(0xFFDC2626)        // Rojo error
//val LightOnError = Color(0xFFFFFFFF)
//
//
// ----- Dark Theme Colors -----
//val DarkPrimary = Color(0xFF3B82F6)       // Azul brillante
//val DarkOnPrimary = Color(0xFF0F172A)     // Azul muy oscuro
//val DarkSecondary = Color(0xFF94A3B8)     // Gris claro
//val DarkOnSecondary = Color(0xFF1E293B)   // Gris oscuro
//val DarkBackground = Color(0xFF0F172A)    // Fondo principal
//val DarkOnBackground = Color(0xFFF8FAFC)  // Texto claro
//val DarkSurface = Color(0xFF1E293B)       // Fondo tarjetas, etc.
//val DarkOnSurface = Color(0xFFE2E8F0)     // Texto sobre superficie
//val DarkError = Color(0xFFF87171)         // Rojo error
//val DarkOnError = Color(0xFF450A0A)

package dev.boni.techhelpdesk.ui.theme

import androidx.compose.ui.graphics.Color

// --- MODO CLARO (Light Mode) ---

// Principales
val LightPrimary = Color(0xFF1976d2)
val LightOnPrimary = Color(0xFFffffff)
val LightPrimaryContainer = Color(0xFFd3e4fd)
val LightOnPrimaryContainer = Color(0xFF001d35)

// Secundarios
val LightSecondary = Color(0xFF5f5e62)
val LightOnSecondary = Color(0xFFffffff)
val LightSecondaryContainer = Color(0xFFe4e1e6)
val LightOnSecondaryContainer = Color(0xFF1b1b1f)

// Terciarios
val LightTertiary = Color(0xFF7d5260)
val LightOnTertiary = Color(0xFFffffff)
val LightTertiaryContainer = Color(0xFFffd8e4)
val LightOnTertiaryContainer = Color(0xFF31111d)

// Fondos y Superficies
val LightBackground = Color(0xFFfafafa)
val LightOnBackground = Color(0xFF1c1b1f)
val LightSurface = Color(0xFFffffff)
val LightOnSurface = Color(0xFF1c1b1f)
val LightSurfaceVariant = Color(0xFFf3f3f3)
val LightOnSurfaceVariant = Color(0xFF49454f)

// Bordes
val LightOutline = Color(0xFF79747e)
val LightOutlineVariant = Color(0xFFcac4d0)

// Error
val LightError = Color(0xFFba1a1a)
val LightOnError = Color(0xFFffffff)
val LightErrorContainer = Color(0xFFffdad6)
val LightOnErrorContainer = Color(0xFF410002)

// --- Colores Semánticos Personalizados (Claro) ---
val LightSuccess = Color(0xFF2e7d32)
val LightOnSuccess = Color(0xFFffffff)
val LightSuccessContainer = Color(0xFFc8e6c9)
val LightOnSuccessContainer = Color(0xFF1b5e20)

val LightWarning = Color(0xFFf57c00)
val LightOnWarning = Color(0xFFffffff)
val LightWarningContainer = Color(0xFFffe0b2)
val LightOnWarningContainer = Color(0xFFe65100)

val LightInfo = Color(0xFF0288d1)
val LightOnInfo = Color(0xFFffffff)
val LightInfoContainer = Color(0xFFb3e5fc)
val LightOnInfoContainer = Color(0xFF01579b)

// Prioridades (Claro)
val LightPriorityCritical = Color(0xFFd32f2f)
val LightPriorityHigh = Color(0xFFf57c00)
val LightPriorityMedium = Color(0xFFfbc02d)
val LightPriorityLow = Color(0xFF388e3c)


// --- MODO OSCURO (Dark Mode) ---

// Principales
val DarkPrimary = Color(0xFFa8c7fa)
val DarkOnPrimary = Color(0xFF003258)
val DarkPrimaryContainer = Color(0xFF004a77)
val DarkOnPrimaryContainer = Color(0xFFd3e4fd)

// Secundarios
val DarkSecondary = Color(0xFFc7c5ca)
val DarkOnSecondary = Color(0xFF313033)
val DarkSecondaryContainer = Color(0xFF47464a)
val DarkOnSecondaryContainer = Color(0xFFe4e1e6)

// Terciarios
val DarkTertiary = Color(0xFFefb8c8)
val DarkOnTertiary = Color(0xFF492532)
val DarkTertiaryContainer = Color(0xFF633b48)
val DarkOnTertiaryContainer = Color(0xFFffd8e4)

// Fondos y Superficies
val DarkBackground = Color(0xFF1c1b1f)
val DarkOnBackground = Color(0xFFe6e1e5)
val DarkSurface = Color(0xFF1c1b1f)
val DarkOnSurface = Color(0xFFe6e1e5)
val DarkSurfaceVariant = Color(0xFF2b2930)
val DarkOnSurfaceVariant = Color(0xFFcac4d0)

// Bordes
val DarkOutline = Color(0xFF938f99)
val DarkOutlineVariant = Color(0xFF49454f)

// Error
val DarkError = Color(0xFFffb4ab)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000a)
val DarkOnErrorContainer = Color(0xFFffdad6)

// --- Colores Semánticos Personalizados (Oscuro) ---
val DarkSuccess = Color(0xFF81c784)
val DarkOnSuccess = Color(0xFF003a00)
val DarkSuccessContainer = Color(0xFF1b5e20)
val DarkOnSuccessContainer = Color(0xFFc8e6c9)

val DarkWarning = Color(0xFFffb74d)
val DarkOnWarning = Color(0xFF4a2800)
val DarkWarningContainer = Color(0xFFe65100)
val DarkOnWarningContainer = Color(0xFFffe0b2)

val DarkInfo = Color(0xFF81d4fa)
val DarkOnInfo = Color(0xFF003547)
val DarkInfoContainer = Color(0xFF01579b)
val DarkOnInfoContainer = Color(0xFFb3e5fc)

// Prioridades (Oscuro)
val DarkPriorityCritical = Color(0xFFef5350)
val DarkPriorityHigh = Color(0xFFff9800)
val DarkPriorityMedium = Color(0xFFffeb3b)
val DarkPriorityLow = Color(0xFF66bb6a)