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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.screens.viewmodels.DashboardUiState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.boni.techhelpdesk.data.model.TicketStatus
import dev.boni.techhelpdesk.data.model.UserRole

/**
 * Pantalla principal del Dashboard.
 *
 * @param navController El controlador de navegación para manejar las acciones.
 * @param viewModel El ViewModel que gestiona el estado de esta pantalla.
 */
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DashboardContent(
        navController = navController,
        uiState = uiState,
        onRefresh = { viewModel.refresh() }
    )
}

@Composable
fun DashboardContent(
    navController: NavController,
    uiState: DashboardUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    val isTechnician = uiState.userRole == UserRole.TECHNICIAN || uiState.userRole == UserRole.ADMIN
    val photoUrl = uiState.userPhotoUrl

    Scaffold(
        topBar = {
            AppHeader(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_greeting, uiState.userName),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.dashboard_welcome_back),
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
                            if (photoUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
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
        val customColors = LocalCustomColors.current

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 24.dp,
                    bottom = innerPadding.calculateBottomPadding() + 24.dp
                ),
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {

                item {
                    SectionTitle(
                        text = stringResource(R.string.dashboard_section_summary),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TicketStatsCard(
                            title = stringResource(R.string.card_open),
                            count = uiState.openCount,
                            icon = Icons.Outlined.ConfirmationNumber,
                            onClick = {
                                navController.navigate("/tickets?status=${TicketStatus.ABIERTO.name.lowercase()}&isTech=${isTechnician}") {
                                    popUpTo(
                                        "/tickets"
                                    ) { inclusive = true }; launchSingleTop = true
                                }
                            },
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            iconBackgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.weight(1f)
                        )
                        TicketStatsCard(
                            title = stringResource(R.string.card_in_progress),
                            count = uiState.inProgressCount,
                            icon = Icons.Outlined.Schedule,
                            onClick = {
                                navController.navigate("/tickets?status=${TicketStatus.EN_PROGRESO.name.lowercase()}&isTech=${isTechnician}") {
                                    popUpTo(
                                        "/tickets"
                                    ) { inclusive = true }; launchSingleTop = true
                                }
                            },
                            color = customColors.warning,
                            contentColor = customColors.onWarning,
                            iconBackgroundColor = customColors.onWarning.copy(alpha = 0.2f),
                            modifier = Modifier.weight(1f)
                        )
                        TicketStatsCard(
                            title = stringResource(R.string.card_closed),
                            count = uiState.closedCount,
                            icon = Icons.Outlined.CheckCircleOutline,
                            onClick = {
                                navController.navigate("/tickets?status=${TicketStatus.CERRADO.name.lowercase()}&isTech=$isTechnician") {
                                    popUpTo(
                                        "/tickets"
                                    ) { inclusive = true }; launchSingleTop = true
                                }
                            },
                            color = customColors.success,
                            contentColor = customColors.onSuccess,
                            iconBackgroundColor = customColors.onSuccess.copy(alpha = 0.2f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    MobileButton(
                        onClick = { navController.navigate("/ticket/create") },
                        variant = MobileButtonVariant.FILLED,
                        fullWidth = true,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.btn_new_ticket),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                item {
                    SectionTitle(
                        text = stringResource(R.string.dashboard_section_quick_access),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    QuickActionGroup(modifier = Modifier.padding(bottom = 24.dp)) {
                        QuickActionItem(
                            icon = Icons.AutoMirrored.Outlined.List,
                            title = stringResource(R.string.action_view_all_title),
                            description = stringResource(R.string.action_view_all_desc),
                            onClick = { navController.navigate("/tickets") },
                            iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        QuickActionItem(
                            icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                            title = stringResource(R.string.action_knowledge_title),
                            description = stringResource(R.string.action_knowledge_desc),
                            onClick = { navController.navigate("/knowledge") },
                            iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        QuickActionItem(
                            icon = Icons.AutoMirrored.Outlined.Chat,
                            title = stringResource(R.string.action_chat_title),
                            description = stringResource(R.string.action_chat_desc),
                            onClick = { navController.navigate("/conversation") },
                            iconBackgroundColor = customColors.successContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()

            DashboardContent(
                navController = navController,
                uiState = DashboardUiState(
                    userName = "Luis (Preview)",
                    openCount = 5,
                    inProgressCount = 2,
                    closedCount = 10,
                    isLoading = false
                ),
                onRefresh = {}
            )
        }
    }
}