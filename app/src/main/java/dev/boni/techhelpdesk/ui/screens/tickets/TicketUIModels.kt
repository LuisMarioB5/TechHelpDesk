package dev.boni.techhelpdesk.ui.screens.tickets

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import dev.boni.techhelpdesk.R // Asegúrate de importar tu R

/**
 * Enum para categorías de tickets
 */
enum class TicketCategoryUI(val value: String, @StringRes val labelResId: Int, val icon: ImageVector) {
    EMAIL("email", R.string.cat_email, Icons.Default.Email),
    HARDWARE("hardware", R.string.cat_hardware, Icons.Default.SettingsSuggest),
    SOFTWARE("software", R.string.cat_software, Icons.Default.Apps),
    RED("red", R.string.cat_network, Icons.Default.Wifi),
    PERMISOS("permisos", R.string.cat_permissions, Icons.Default.Lock),
    OTRO("otro", R.string.cat_other, Icons.Default.MoreHoriz);

    companion object {
        fun fromString(value: String?): TicketCategoryUI? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Enum para estados de tickets
 */
enum class TicketStatusUI(val value: String, @StringRes val labelResId: Int, val icon: ImageVector) {
    ABIERTO("abierto", R.string.status_open, Icons.Default.RadioButtonUnchecked),
    EN_PROGRESO("en_progreso", R.string.status_in_progress, Icons.Default.Pending),
    RESUELTO("resuelto", R.string.status_resolved, Icons.Default.CheckCircle),
    CERRADO("cerrado", R.string.status_closed, Icons.Default.CheckCircle);

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
 * Enum para prioridades de tickets
 */
enum class TicketPriorityUI(val value: String, @StringRes val labelResId: Int, val icon: ImageVector) {
    ALTA("alta", R.string.prio_high, Icons.Default.PriorityHigh),
    MEDIA("media", R.string.prio_medium, Icons.Default.DragHandle),
    BAJA("baja", R.string.prio_low, Icons.Default.ArrowDownward);

    companion object {
        fun fromString(value: String?): TicketPriorityUI? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}

// Opciones de filtro (incluye null para "Todas" las opciones del filtro)
val statusFilterOptions = listOf(null) + TicketStatusUI.entries
val priorityFilterOptions = listOf(null) + TicketPriorityUI.entries
val categoryFilterOptions = listOf(null) + TicketCategoryUI.entries