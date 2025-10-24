package dev.boni.techhelpdesk.ui.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.boni.techhelpdesk.ui.components.SectionTitle
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

/**
 * Un contenedor horizontal (LazyRow) para mostrar las tarjetas
 * de [TicketStatsCard] en el dashboard.
 *
 * Proporciona el espaciado horizontal correcto.
 *
 * @param modifier Modificador de Compose.
 * @param content El contenido del grupo, típicamente una lista de [TicketStatsCard]s.
 */
@Composable
fun TicketStatsGroup(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        // --- Centra los items Y les da espaciado ---
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(horizontal = 16.dp), // Padding del 'main'
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun TicketStatsGroupPreview() {
    // --- Colores de ejemplo para la Preview ---
    // (Estos colores no están en tu tema, son solo para simular la imagen)
    val warningColor = Color(0xFFF59E0B) // Naranja/Ámbar
    val onWarningColor = Color(0xFF000000) // Negro
    val successColor = Color(0xFF10B981) // Verde
    val onSuccessColor = Color(0xFFFFFFFF) // Blanco

    TechHelpDeskTheme {
        Column(Modifier.padding(vertical = 16.dp)) {
            // 1. Título de la Sección
            SectionTitle(text = "Resumen de tickets")

            // 2. Grupo de Tarjetas
            TicketStatsGroup {
                // 3. Las 3 Tarjetas de Estadísticas
                item {
                    TicketStatsCard(
                        title = "Abiertos",
                        count = 12,
                        icon = Icons.Outlined.ConfirmationNumber,
                        onClick = { },
                        // Colores Primary (de tu tema)
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        iconBackgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    )
                }
                item {
                    TicketStatsCard(
                        title = "En progreso",
                        count = 5,
                        icon = Icons.Outlined.Schedule,
                        onClick = { },
                        // Colores Warning (de ejemplo)
                        color = warningColor,
                        contentColor = onWarningColor,
                        iconBackgroundColor = onWarningColor.copy(alpha = 0.2f)
                    )
                }
                item {
                    TicketStatsCard(
                        title = "Cerrados",
                        count = 28,
                        icon = Icons.Outlined.CheckCircleOutline,
                        onClick = { },
                        // Colores Success (de ejemplo)
                        color = successColor,
                        contentColor = onSuccessColor,
                        iconBackgroundColor = onSuccessColor.copy(alpha = 0.2f)
                    )
                }
            }

            // 3. Call to Action
            QuickActionCard(
                icon = Icons.Outlined.AddCircleOutline,
                title = "Ver Todos los Tickets",
                description = "Gestiona todas tus solicitudes.",
                onClick = { },
                Modifier.padding(vertical = 16.dp),
            )
        }
    }
}
