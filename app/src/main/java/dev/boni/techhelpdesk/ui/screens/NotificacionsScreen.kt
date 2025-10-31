package dev.boni.techhelpdesk.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- Definiciones de Datos ---
data class NotificationData(
    val id: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color,
    val title: String,
    val description: String,
    val time: String,
    val ticketId: String?,
    var isRead: Boolean // Cambiado a 'var' para poder modificarlo
)

// --- Mapeo de Colores Helper ---
@Composable
fun getNotificationColors(colorKey: String): Pair<Color, Color> {
    val customColors = LocalCustomColors.current
    return when (colorKey) {
        "primary" -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
        "warning" -> Pair(customColors.warningContainer, customColors.warning)
        "success" -> Pair(customColors.successContainer, customColors.success)
        "error" -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


// --- Pantalla Principal de Notificaciones ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val (primaryBg, primaryIcon) = getNotificationColors("primary")
    val (warningBg, warningIcon) = getNotificationColors("warning")
    val (successBg, successIcon) = getNotificationColors("success")
    val (orangeBg, orangeIcon) = getNotificationColors("warning")

    // --- Estado ---
    val notifications = remember {
        mutableStateListOf( // Usamos mutableStateListOf para que la UI reaccione a los cambios de 'isRead'
            NotificationData("1", Icons.Default.Sync, primaryIcon, primaryBg, "Ticket #1245 actualizado", "El técnico Juan Pérez cambió el estado a En progreso", "hace 2h", "1245", false),
            NotificationData("2", Icons.AutoMirrored.Filled.Chat, warningIcon, warningBg, "Nuevo comentario", "Has recibido un mensaje del técnico en el ticket #1260", "hace 4h", "1260", false),
            NotificationData("4", Icons.Default.PersonAdd, primaryIcon, primaryBg, "Nuevo ticket asignado", "Se te ha asignado el técnico Laura García para el ticket #1280", "hace 3 días", "1280", false),
            NotificationData("3", Icons.Default.CheckCircle, successIcon, successBg, "Ticket cerrado", "Tu ticket #1220 ha sido marcado como resuelto", "hace 1 día", "1220", true),
            NotificationData("5", Icons.Default.NotificationsActive, orangeIcon, orangeBg, "Recordatorio", "Tienes un ticket pendiente de cerrar (#1270)", "hace 5 días", "1270", true)
        )
    }

    val unreadCount by remember { derivedStateOf { notifications.count { !it.isRead } } }

    // --- Lógica ---
    val handleNotificationClick = { notification: NotificationData ->
        // Marcar como leída
        val index = notifications.indexOfFirst { it.id == notification.id }
        if (index != -1 && !notifications[index].isRead) {
            // Modificamos la propiedad 'isRead' del objeto en la lista
            notifications[index] = notifications[index].copy(isRead = true)
        }
        // Navegar
        notification.ticketId?.let {
            navController.navigate("/ticket/detail/$it")
        }
    }

    val markAllAsRead = {
        // Iteramos y actualizamos la lista
        notifications.indices.forEach { i ->
            if (!notifications[i].isRead) {
                notifications[i] = notifications[i].copy(isRead = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(
                                    "$unreadCount ${if (unreadCount > 1) "nuevas" else "nueva"}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                bottomContent = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = markAllAsRead,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Marcar todas como leídas", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- ¡¡ARREGLO 3: 'notifications' ahora existe!! ---
            items(notifications, key = { it.id }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { handleNotificationClick(notification) }
                )
            }
        }
    }
}

// --- Componente Helper para el Item de Notificación ---
@Composable
fun NotificationItem(
    notification: NotificationData,
    onClick: () -> Unit
) {
    val titleWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
    val textColor = if (!notification.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val itemBorder = if (!notification.isRead) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = itemBorder
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Icono ---
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(notification.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = notification.icon,
                        contentDescription = null,
                        tint = notification.iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }
            } // Fin Box Icono

            // --- Contenido de Texto ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = titleWeight,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                    Text(notification.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // --- Chevron ---
            if (notification.ticketId != null) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            NotificationsScreen(navController = navController)
        }
    }
}