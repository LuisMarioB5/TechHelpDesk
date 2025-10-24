package dev.boni.techhelpdesk.ui.components.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importa tu tema para el Preview
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- CAMBIO: Enum 'TicketStatsColor' eliminado ---

/**
 * Tarjeta de estadísticas de tickets.
 *
 * @param title Título de la tarjeta.
 * @param count Número a mostrar.
 * @param icon Icono (ImageVector) a mostrar.
 * @param onClick Acción al pulsar.
 * @param modifier Modificador de Compose.
 * @param color Color de fondo de la tarjeta (Por defecto: primary).
 * @param contentColor Color del texto y el icono (Por defecto: onPrimary).
 * @param iconBackgroundColor Color del fondo del círculo del icono (Por defecto: onPrimary con alpha).
 */
@Composable
fun TicketStatsCard(
    title: String,
    count: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
) {

    // Efecto de escala al presionar
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = MaterialTheme.shapes.large,
        color = color,
        contentColor = contentColor,
        shadowElevation = 4.dp,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBackgroundColor, CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                lineHeight = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.9f),
                lineHeight = 14.sp
            )
        }
    }
}

// --- VISTA PREVIA (AÑADIDA) ---
@Preview(showBackground = true, name = "Ticket Stats Card Preview")
@Composable
fun TicketStatsCardPreview() {
    TechHelpDeskTheme {
        Column(
//            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Uso por defecto (usará los colores primary/onPrimary)
            TicketStatsCard(
                title = "Tickets Abiertos",
                count = 12,
                icon = Icons.Default.List,
                onClick = { }
            )

            // 2. Uso personalizado (pasando los colores 'secondary')
            TicketStatsCard(
                title = "Tickets Urgentes",
                count = 3,
                icon = Icons.Default.Warning,
                onClick = { },
                color = MaterialTheme.colorScheme.secondary, // Tu sustituto de 'warning'
                contentColor = MaterialTheme.colorScheme.onSecondary,
                iconBackgroundColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)
            )

            // 3. Uso personalizado (pasando los colores 'error')
            TicketStatsCard(
                title = "Tickets Resueltos",
                count = 42,
                icon = Icons.Default.CheckCircle,
                onClick = { },
                color = MaterialTheme.colorScheme.error, // Tu sustituto de 'success'
                contentColor = MaterialTheme.colorScheme.onError,
                iconBackgroundColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.2f)
            )
        }
    }
}
