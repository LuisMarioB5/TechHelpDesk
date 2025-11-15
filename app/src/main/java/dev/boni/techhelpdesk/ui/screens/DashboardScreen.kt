package dev.boni.techhelpdesk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
import dev.boni.techhelpdesk.ui.components.SectionTitle
import dev.boni.techhelpdesk.ui.components.dashboard.QuickActionGroup
import dev.boni.techhelpdesk.ui.components.dashboard.QuickActionItem
import dev.boni.techhelpdesk.ui.components.dashboard.TicketStatsCard
import dev.boni.techhelpdesk.ui.screens.viewmodels.DashboardViewModel
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow // Para la preview
import kotlinx.coroutines.flow.asStateFlow // Para la preview

/**
 * Pantalla principal del Dashboard.
 *
 * @param navController El controlador de navegación para manejar las acciones.
 * @param viewModel El ViewModel que gestiona el estado de esta pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {
    // Observa el nombre de usuario desde el ViewModel ---
    val userName by viewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(
                title = {
                    Column {
                        Text(
                            // Usa el nombre de usuario del ViewModel ---
                            text = "Hola, $userName",
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
                        onClick = { navController.navigate("/profile") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        // Pasa el ViewModel al contenido ---
        DashboardContent(
            navController = navController,
            innerPadding = innerPadding,
            viewModel = viewModel // Pasa el ViewModel
        )
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: DashboardViewModel // <-- Recibe el ViewModel
) {
    // Obtenemos los colores personalizados (esto no cambia)
    val customColors = LocalCustomColors.current

    // (Opcional: Podrías observar más estados aquí, ej.
    // val stats by viewModel.ticketStats.collectAsState() )

    LazyColumn(
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 24.dp,
            bottom = innerPadding.calculateBottomPadding() + 24.dp // Añadido padding inferior
        ),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {

        // --- SECCIÓN 1: Resumen de tickets ---
        item {
            SectionTitle(
                text = "Resumen de tickets",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                TicketStatsCard(
                    title = "Abiertos",
                    count = 12, // TODO En el futuro esto vendra del viewModel de los tickets
                    icon = Icons.Outlined.ConfirmationNumber,
                    onClick = { navController.navigate("/tickets?status=abierto") { popUpTo("/tickets"){ inclusive = true }; launchSingleTop = true } },
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    iconBackgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
                TicketStatsCard(
                    title = "En progreso",
                    count = 5, // TODO En el futuro esto vendra del viewModel de los tickets
                    icon = Icons.Outlined.Schedule,
                    onClick = { navController.navigate("/tickets?status=en_progreso") { popUpTo("/tickets"){ inclusive = true }; launchSingleTop = true } },
                    color = customColors.warning,
                    contentColor = customColors.onWarning,
                    iconBackgroundColor = customColors.onWarning.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
                TicketStatsCard(
                    title = "Cerrados",
                    count = 28, // TODO En el futuro esto vendra del viewModel de los tickets
                    icon = Icons.Outlined.CheckCircleOutline,
                    onClick = { navController.navigate("/tickets?status=cerrado") { popUpTo("/tickets"){ inclusive = true }; launchSingleTop = true } },
                    color = customColors.success,
                    contentColor = customColors.onSuccess,
                    iconBackgroundColor = customColors.onSuccess.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- SECCIÓN 2: Botón de Nuevo Ticket ---
        item {
            MobileButton(
                onClick = { navController.navigate("/ticket/create") },
                variant = MobileButtonVariant.FILLED,
                fullWidth = true,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Nuevo ticket", style = MaterialTheme.typography.labelLarge)
            }
        }

        // --- SECCIÓN 3: Accesos Rápidos ---
        item {
            SectionTitle(
                text = "Accesos rápidos",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item {
            QuickActionGroup(modifier = Modifier.padding(bottom = 24.dp)) {
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.List,
                    title = "Ver todos los tickets",
                    description = "Gestiona tus solicitudes",
                    onClick = { navController.navigate("/tickets") },
                    iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                    title = "Base de conocimiento",
                    description = "Encuentra respuestas rápidas",
                    onClick = { navController.navigate("/knowledge") },
                    iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                QuickActionItem(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    title = "Chat de soporte",
                    description = "Habla con un técnico",
                    onClick = { navController.navigate("/support-chat") },
                    iconBackgroundColor = customColors.successContainer,
                    contentColor = customColors.onSuccessContainer
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
        // La Preview ahora necesita un ViewModel falso ---
        // 1. Creamos una implementación falsa (Stub) de la clase
        class PreviewDashboardViewModel : DashboardViewModel() {
            // Sobrescribimos el estado para la preview
            override val userName: StateFlow<String> = MutableStateFlow("Luis (Preview)").asStateFlow()
        }

        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            // 2. Pasamos el ViewModel falso a la pantalla
            DashboardScreen(
                navController = navController,
                viewModel = PreviewDashboardViewModel()
            )
        }
    }
}