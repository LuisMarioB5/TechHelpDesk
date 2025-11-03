// ConversationsScreen.kt (Corregido)

package dev.boni.techhelpdesk.ui.screens // O el paquete correcto

// import androidx.compose.foundation.BorderStroke // No se usa
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // <-- IMPORTACIÓN AÑADIDA
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat // Importación corregida
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.* // Importar todos
import androidx.compose.material.icons.outlined.* // Importar todos outlined
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.BottomNavigation
// import dev.boni.techhelpdesk.ui.components.MobileButton // Reemplazado por Button
// import dev.boni.techhelpdesk.ui.components.MobileButtonVariant // Reemplazado por Button
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- Definiciones de Datos ---
data class Conversation(
    val id: String,
    val technicianName: String,
    val technicianAvatar: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean
)

// --- Datos de Ejemplo ---
val sampleConversations = listOf(
    Conversation("1", "Carlos Méndez", "C", "Perfecto, verifica por favor...", "hace 1h", 1, true),
    Conversation("2", "Ana García", "A", "Recibido, lo revisaré de inmediato.", "hace 3h", 0, false),
    Conversation("3", "Luis Torres", "L", "Tu ticket #1220 ha sido cerrado.", "hace 1 día", 0, true)
)

// --- Pantalla Principal de Conversaciones ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado ---
    var searchQuery by remember { mutableStateOf("") }

    val filteredConversations by remember(searchQuery, sampleConversations) {
        derivedStateOf {
            // --- CAMBIO: Usar sampleConversations ---
            sampleConversations.filter { conv ->
                conv.technicianName.contains(searchQuery, ignoreCase = true) ||
                        conv.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val totalUnread = remember(sampleConversations) {
        // --- CAMBIO: Usar sampleConversations ---
        sampleConversations.sumOf { it.unreadCount }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                navigationIcon = null,
                title = {
                    Column {
                        Text(
                            "Chat de Soporte",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = if (totalUnread > 0) "$totalUnread mensaje${if (totalUnread > 1) "s" else ""} nuevo${if (totalUnread > 1) "s" else ""}" else "Todas tus conversaciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("/conversation/new") },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Nuevo chat",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                bottomContent = {
                    // --- CAMBIO: Barra de búsqueda envuelta en Surface para sombra ---
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp), // rounded-[16px]
                        shadowElevation = 4.dp // shadow-md
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar conversaciones...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Mismo color
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            // elevation = ... <-- Esta línea no existe en OutlinedTextField
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                top = 24.dp + 16.dp, // Espacio para el search bar flotante
                bottom = 24.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            if (filteredConversations.isEmpty()) {
                item {
                    EmptyChatState(
                        searchQuery = searchQuery,
                        onClearSearch = { searchQuery = "" },
                        onStartChat = { navController.navigate("/conversation/new") }
                    )
                }
            } else {
                items(filteredConversations, key = { it.id }) { conversation ->
                    ConversationListItem(
                        conversation = conversation,
                        onClick = {
                            navController.navigate("/conversation/detail/${conversation.id}")
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// --- Componente Helper: Item de la Lista ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListItem(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BadgedBox(
                badge = {
                    if (conversation.isOnline) {
                        Badge(
                            modifier = Modifier
                                .size(14.dp)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            containerColor = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        conversation.technicianAvatar,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.technicianName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = conversation.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (conversation.unreadCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                conversation.unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Componente Helper: Estado Vacío ---
@Composable
fun EmptyChatState(
    searchQuery: String,
    onClearSearch: () -> Unit,
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.Chat, // Icono de Chat (Outlined)
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = if (searchQuery.isNotBlank()) "No se encontraron conversaciones" else "No tienes conversaciones",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = if (searchQuery.isNotBlank()) "Intenta con otros términos de búsqueda" else "Inicia un nuevo chat con un técnico para obtener ayuda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp).widthIn(max = 280.dp)
        )
        if (searchQuery.isBlank()) {
            // --- CAMBIO: Reemplazado MobileButton por Button ---
            Button(
                onClick = onStartChat,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Iniciar chat", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ConversationsScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            ConversationsScreen(navController = navController)
        }
    }
}