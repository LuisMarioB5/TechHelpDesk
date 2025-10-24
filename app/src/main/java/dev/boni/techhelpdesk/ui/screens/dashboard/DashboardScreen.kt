package dev.boni.techhelpdesk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.components.SectionTitle
import dev.boni.techhelpdesk.ui.components.dashboard.QuickActionCard
import dev.boni.techhelpdesk.ui.components.dashboard.QuickActionGroup
import dev.boni.techhelpdesk.ui.components.dashboard.QuickActionItem
import dev.boni.techhelpdesk.ui.components.dashboard.TicketStatsCard
import dev.boni.techhelpdesk.ui.components.dashboard.TicketStatsGroup
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightOnSuccess
import dev.boni.techhelpdesk.ui.theme.LightOnWarning
import dev.boni.techhelpdesk.ui.theme.LightSuccess
import dev.boni.techhelpdesk.ui.theme.LightWarning
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

/**
 * Pantalla principal del Dashboard.
 *
 * @param navController El controlador de navegación para manejar las acciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController
) {
    // Usamos un Scaffold para la estructura de pantalla estándar de Material 3
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigation(navController = navController)
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            DashboardContent(
                modifier = Modifier.fillMaxSize(), // <-- Que llene todo
                navController = navController
            )

            AppHeader(
                title = {
                    Column {
                        Text(
                            text = "Hola, Luis",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Bienvenido de vuelta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* navController.navigate("/profile") */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Obtenemos los colores personalizados que provee el Tema
    // (Asegúrate de haber modificado tu Theme.kt para proveerlos)
    val customColors = LocalCustomColors.current

    // Usamos LazyColumn para que la pantalla sea scrollable
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        // Padding horizontal para todo el contenido
//        contentPadding = PaddingValues(vertical = 24.dp), todo descomentar
        // Espacio vertical entre las secciones (como el space-y-6 de React)
        verticalArrangement = Arrangement.spacedBy(24.dp),

    ) {

        // --- SECCIÓN 1: Resumen de tickets ---
        item {
            SectionTitle(
                text = "Resumen de tickets",
            )
        }
        item {
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
                    color = customColors.warning,
                    contentColor = customColors.onWarning,
                    iconBackgroundColor = customColors.onWarning.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )

                TicketStatsCard(
                    title = "Cerrados",
                    count = 28,
                    icon = Icons.Outlined.CheckCircleOutline,
                    onClick = { },
                    // Colores Success (de ejemplo)
                    color = customColors.success,
                    contentColor = customColors.onSuccess,
                    iconBackgroundColor = customColors.onSuccess.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- SECCIÓN 2: Botón de Nuevo Ticket ---
        item {
            QuickActionCard(
                icon = Icons.Outlined.AddCircleOutline,
                title = "Ver Todos los Tickets",
                description = null,
                onClick = { },
            )
        }

        // --- SECCIÓN 3: Accesos Rápidos ---
        item {
            SectionTitle(
                text = "Accesos rápidos",
            )
        }
        item {
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

// --- VISTA PREVIA ---
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    TechHelpDeskTheme {
        // Para que la preview funcione, "proveemos" los colores personalizados
        // (En tu app real, esto lo harás en tu 'Theme.kt' principal)
        val customColors = CustomColors(
            success = LightSuccess,
            onSuccess = LightOnSuccess,
            successContainer = MaterialTheme.colorScheme.tertiaryContainer, // Usamos un sustituto para el preview
            onSuccessContainer = MaterialTheme.colorScheme.onTertiaryContainer,
            warning = LightWarning,
            onWarning = LightOnWarning,
            warningContainer = MaterialTheme.colorScheme.secondaryContainer, // Usamos un sustituto para el preview
            onWarningContainer = MaterialTheme.colorScheme.onSecondaryContainer
        )

        CompositionLocalProvider {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }
    }
}
