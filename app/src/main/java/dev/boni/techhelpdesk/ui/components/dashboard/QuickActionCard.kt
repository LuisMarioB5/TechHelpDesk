package dev.boni.techhelpdesk.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme


/**
 * Tarjeta de acción rápida, estilo item de lista.
 *
 * @param icon Icono (ImageVector) a mostrar.
 * @param title Título de la acción.
 * @param description Descripción corta de la acción. (Ahora opcional)
 * @param onClick Acción al pulsar.
 * @param modifier Modificador de Compose.
 * @param iconBackgroundColor Color de fondo para el círculo del icono.
 * @param iconColor Color para el icono.
 */
@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    description: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    needChevron: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(iconBackgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Icono
        Box(
            modifier = Modifier
                .size(44.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        // Texto
        Column(
//            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                lineHeight = 18.sp
            )

            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    lineHeight = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Chevron
        if (needChevron) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Con Descripción (Default)")
@Composable
fun QuickActionCardPreview() {
    // Asegúrate de importar tu TechHelpDeskTheme
    TechHelpDeskTheme {
        QuickActionCard(
            // --- CAMBIO: Usamos el icono sin relleno ---
            icon = Icons.Outlined.AddCircleOutline,
            title = "Ver Todos los Tickets",
            description = "Gestiona todas tus solicitudes.",
            onClick = { }
            // No se pasan colores, usa los defaults (primaryContainer)
        )
    }
}

@Preview(showBackground = true, name = "Sin Descripción (Error)")
@Composable
fun QuickActionCardPreviewNoDesc() {
    // Asegúrate de importar tu TechHelpDeskTheme
    TechHelpDeskTheme {
        QuickActionCard(
            // --- CAMBIO: Dejado como 'Default' (que es 'Filled') para ver la diferencia ---
            icon = Icons.Default.AddCircle,
            title = "Ver Alertas Urgentes",
            description = null, // <-- Ahora funciona al pasar null
            onClick = { },
            // --- CAMBIO: Pasamos los colores 'error' directamente ---
            iconBackgroundColor = MaterialTheme.colorScheme.errorContainer,
            color = MaterialTheme.colorScheme.onErrorContainer,
            needChevron = true
        )
    }
}
