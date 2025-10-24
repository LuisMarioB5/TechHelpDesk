package dev.boni.techhelpdesk.ui.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
    // --- CAMBIO: El contenido ahora es un RowScope ---
    // Esto permite que los items de adentro usen Modifier.weight()
    content: @Composable RowScope.() -> Unit
) {
    Row (
        modifier = modifier
            .fillMaxWidth(),
            // --- CAMBIO: Añadimos padding para que no se pegue a los bordes ---
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        Column() {
            // 1. Título de la Sección
            SectionTitle(text = "Resumen de tickets")

            // 2. Grupo de Tarjetas
            TicketStatsGroup {
                // 3. Las 3 Tarjetas de Estadísticas
                TicketStatsCard(
                    title = "Abiertos",
                    count = 12,
                    icon = Icons.Outlined.ConfirmationNumber,
                    onClick = { },
                    // Colores Primary (de tu tema)
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    iconBackgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )

                TicketStatsCard(
                    title = "En progreso",
                    count = 5,
                    icon = Icons.Outlined.Schedule,
                    onClick = { },
                    // Colores Warning (de ejemplo)
                    color = warningColor,
                    contentColor = onWarningColor,
                    iconBackgroundColor = onWarningColor.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )

                TicketStatsCard(
                    title = "Cerrados",
                    count = 28,
                    icon = Icons.Outlined.CheckCircleOutline,
                    onClick = { },
                    // Colores Success (de ejemplo)
                    color = successColor,
                    contentColor = onSuccessColor,
                    iconBackgroundColor = onSuccessColor.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Call to Action
            QuickActionCard(
                icon = Icons.Outlined.AddCircleOutline,
                title = "Ver Todos los Tickets",
                description = null,
                onClick = { },
                Modifier.padding(vertical = 16.dp),
            )
        }
    }
}
