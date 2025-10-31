package dev.boni.techhelpdesk.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.boni.techhelpdesk.ui.components.SectionTitle
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

/**
 * Un contenedor para una lista de [QuickActionCard]s.
 *
 * Proporciona el fondo de superficie, el redondeo y el espaciado
 * vertical correctos.
 *
 * @param modifier Modificador de Compose.
 * @param content El contenido del grupo, típicamente una lista de [QuickActionCard]s.
 */
@Composable
fun QuickActionGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large, // rounded-[24px] (large suele ser 24.dp)
        shadowElevation = 1.dp // shadow-sm
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // p-4 (1rem = 16dp)
            verticalArrangement = Arrangement.spacedBy(8.dp), // space-y-2 (2 * 4dp = 8dp)
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuickActionGroupPreview() {
    TechHelpDeskTheme {
        Column(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            SectionTitle(text = "Accesos rápidos")
            QuickActionGroup {
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.List,
                    title = "Ver todos los tickets",
                    description = "Gestiona tus solicitudes",
                    onClick = { }
                    // Colores por defecto (Primary)
                )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    title = "Base de conocimiento",
                    description = "Encuentra respuestas rápidas",
                    onClick = { },
                    // Traducción de 'secondary'
                    iconBackgroundColor = MaterialTheme.colorScheme.secondary,
                )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    title = "Chat de soporte",
                    description = "Habla con un técnico",
                    onClick = { },
                    iconBackgroundColor = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
