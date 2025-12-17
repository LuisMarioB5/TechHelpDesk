package dev.boni.techhelpdesk.ui.screens.tickets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum para categorías de tickets con display name e iconos para la UI
 */
enum class TicketCategoryUI(val value: String, val displayName: String, val icon: ImageVector) {
    EMAIL("email", "Email", Icons.Default.Email),
    HARDWARE("hardware", "Hardware", Icons.Default.SettingsSuggest),
    SOFTWARE("software", "Software", Icons.Default.Apps),
    RED("red", "Red", Icons.Default.Wifi),
    PERMISOS("permisos", "Permisos", Icons.Default.Lock),
    OTRO("otro", "Otro", Icons.Default.MoreHoriz);

    companion object {
        fun fromString(value: String?): TicketCategoryUI? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Enum para estados de tickets con display name e iconos para la UI
 */
enum class TicketStatusUI(val value: String, val displayName: String, val icon: ImageVector) {
    ABIERTO("abierto", "Abierto", Icons.Default.RadioButtonUnchecked),
    EN_PROGRESO("en_progreso", "En progreso", Icons.Default.Pending),
    RESUELTO("resuelto", "Resuelto", Icons.Default.CheckCircle),
    CERRADO("cerrado", "Cerrado", Icons.Default.CheckCircle);

    companion object {
        fun fromString(value: String?): TicketStatusUI? {
            if (value == null) return null
            return entries.find {
                it.value.equals(value.replace("-", "_"), ignoreCase = true)
            }
        }
    }
}

/**
 * Enum para prioridades de tickets con display name e iconos para la UI
 */
enum class TicketPriorityUI(val value: String, val displayName: String, val icon: ImageVector) {
    ALTA("alta", "Alta", Icons.Default.PriorityHigh),
    MEDIA("media", "Media", Icons.Default.DragHandle),
    BAJA("baja", "Baja", Icons.Default.ArrowDownward);

    companion object {
        fun fromString(value: String?): TicketPriorityUI? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}

// Opciones de filtro (incluye null para "Todos/Todas")
val statusFilterOptions = listOf(null) + TicketStatusUI.entries
val priorityFilterOptions = listOf(null) + TicketPriorityUI.entries
val categoryFilterOptions = listOf(null) + TicketCategoryUI.entries