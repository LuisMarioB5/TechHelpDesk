package dev.boni.techhelpdesk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.List
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
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
    // --- CAMBIO: Aplicada la Solución Definitiva ---
    Scaffold(
        // 1. AppHeader vuelve al topBar
        topBar = {
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
                    // --- ¡CAMBIO AQUÍ! ---
                    IconButton(
                        onClick = { navController.navigate("/profile") },
                        // El IconButton ya no necesita fondo, solo tamaño si quieres un área de clic mayor
                        modifier = Modifier.size(48.dp) // Área de clic
                    ) {
                        // 1. Envolvemos el Icon en un Box
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Tamaño del círculo de fondo
                                .clip(CircleShape)
                                .background(
                                    // Color de fondo semitransparente
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center // Centra el Icon dentro del Box
                        ) {
                            // 2. El Icon ahora va dentro del Box
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                // 3. Tintamos el icono y le damos tamaño
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp) // Tamaño del icono
                            )
                        }
                    }
                    // --- FIN DEL CAMBIO ---
                }
            )
        },
        // 2. Fondo del Scaffold es Transparente
        containerColor = Color.Transparent
    ) { innerPadding ->
        // 3. Pasamos el innerPadding (que ahora incluye la altura del header)
        //    directamente al contenido.
        DashboardContent(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    innerPadding: PaddingValues
) {
    // Obtenemos los colores personalizados que provee el Tema
    val customColors = LocalCustomColors.current

    // Usamos LazyColumn para que la pantalla sea scrollable
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            // 4. El LazyColumn dibuja el fondo
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        // 5. El padding del Scaffold (top y bottom) se aplica al *contenido* de la lista
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 24.dp, // <-- Espacio entre header y contenido
            bottom = innerPadding.calculateBottomPadding()
        ),
        // Espacio vertical entre las secciones
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
                    onClick = { navController.navigate("/tickets?status=abierto") },
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    iconBackgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
                TicketStatsCard(
                    title = "En progreso",
                    count = 5,
                    icon = Icons.Outlined.Schedule,
                    onClick = { navController.navigate("/tickets?status=en_progreso") },
                    color = customColors.warning,
                    contentColor = customColors.onWarning,
                    iconBackgroundColor = customColors.onWarning.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
                TicketStatsCard(
                    title = "Cerrados",
                    count = 28,
                    icon = Icons.Outlined.CheckCircleOutline,
                    onClick = { navController.navigate("/tickets?status=cerrado") },
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
                title = "Crear nuevo ticket",
                description = null,
                onClick = { navController.navigate("/ticket/create") },
            )
        }

        // --- SECCIÓN 3: Accesos Rápidos ---
        item {
            SectionTitle(
                text = "Accesos rápidos",
            )
        }
        item {
            // CAMBIO: Añadido padding horizontal al grupo
            QuickActionGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.List,
                    title = "Ver todos los tickets",
                    description = "Gestiona tus solicitudes",
                    onClick = { navController.navigate("/tickets") },
                    )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                    title = "Base de conocimiento",
                    description = "Encuentra respuestas rápidas",
                    onClick = { },
                    // CAMBIO: Usamos secondaryContainer (más sutil)
                    iconBackgroundColor = MaterialTheme.colorScheme.secondary,
                )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    title = "Chat de soporte",
                    description = "Habla con un técnico",
                    onClick = { },
                    // CAMBIO: Usamos successContainer (más sutil y correcto)
                    iconBackgroundColor = customColors.success,
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

        CompositionLocalProvider(LocalCustomColors provides customColors) {
            val navController = rememberNavController()
            DashboardScreen(navController = navController)
        }
    }
}

