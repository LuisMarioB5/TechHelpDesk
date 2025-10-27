package dev.boni.techhelpdesk.ui.screens.tickets.id

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader // Reutilizamos AppHeader
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors // Usado en Preview
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Muestra los detalles de un ticket específico y la conversación asociada.
 *
 * @param navController Controlador de navegación para manejar acciones como volver atrás.
 * @param ticketId El ID del ticket cuyos detalles se deben mostrar.
 * @param modifier Modificador de Compose opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    navController: NavController,
    ticketId: String,
    modifier: Modifier = Modifier
) {
    var newMessage by remember { mutableStateOf("") }
    var hasConversation by remember { mutableStateOf(false) } // Cambia a true para ver la conversación
    val sampleMessages = remember { // Mensajes de ejemplo si hasConversation es true
        listOf(
            // Re-usamos la estructura de Message pero con tipos básicos si es necesario
            mapOf("sender" to "user", "senderName" to "Luis Rodríguez", "text" to "Hola, tengo problemas con mi impresora, no imprime.", "time" to "10:30 AM"),
            mapOf("sender" to "technician", "senderName" to "Carlos Méndez", "text" to "Entendido. ¿Podrías confirmar si está conectada vía Wi-Fi o cable USB?", "time" to "10:35 AM"),
            mapOf("sender" to "user", "senderName" to "Luis Rodríguez", "text" to "Está conectada por Wi-Fi.", "time" to "10:37 AM"),
            mapOf("sender" to "technician", "senderName" to "Carlos Méndez", "text" to "Perfecto, verifica por favor si la red es la misma del equipo.", "time" to "10:40 AM"),
        )
    }
    var messages by remember { mutableStateOf(if (hasConversation) sampleMessages else emptyList()) }


    // --- Funciones de Lógica (Simplificadas para la preview) ---
    val handleSendMessage = {
        if (newMessage.isNotBlank()) {
            val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val msg = mapOf(
                "sender" to "user",
                "senderName" to "Luis Rodríguez",
                "text" to newMessage,
                "time" to currentTime
            )
            messages = messages + msg
            newMessage = "" // Limpia el input
            if (!hasConversation) hasConversation = true
        }
    }
    val handleStartConversation = { hasConversation = true }

    // Obtenemos colores custom (Necesario para los componentes helper)
    val customColors = LocalCustomColors.current

    // --- DATOS HARCODEADOS PARA LA PREVIEW ---
    val ticketId = "T-2025-004" // ID del ticket de ejemplo
    val ticketTitle = "Correo no envía archivos adjuntos"
    val ticketStatus = "Abierto" // Usar String directamente
    val ticketPriority = "Alta" // Usar String directamente
    val ticketCategory = "Email" // Usar String directamente
    val reportedBy = "Luis Rodríguez"
    val assignedTo = "Carlos Méndez"
    val createdDate = "15 Ene 2025, 10:30 AM"
    val description = "Al intentar enviar correos con archivos adjuntos de más de 5MB, el sistema muestra un error y no permite completar el envío. He intentado con diferentes tipos de archivos (PDF, Excel, Word) y el problema persiste."
    // --- FIN DATOS HARCODEADOS ---


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            ) {
                AppHeader(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Ticket #$ticketId", // Usar String
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            // Usamos el componente StatusChip, pasándole el String
                            StatusChipPreviewHelper(status = ticketStatus, customColors = customColors, size = "large")
                        }
                    },
                    bottomContent = {
                        Text(
                            text = ticketTitle, // Usar String
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Tarjeta de Detalles ---
            item {
                TicketInfoCardPreviewHelper(
                    category = ticketCategory, // String
                    priority = ticketPriority, // String
                    reportedBy = reportedBy, // String
                    assignedTo = assignedTo, // String
                    createdDate = createdDate, // String
                    description = description, // String
                    customColors = customColors
                )
            }

            // --- Tarjeta de Conversación ---
            item {
                ConversationCardPreviewHelper(
                    messages = messages, // Usamos la lista de mapas
                    newMessage = newMessage,
                    onNewMessageChange = { newMessage = it }, // Corregido
                    onSendMessage = handleSendMessage,
                    hasConversation = hasConversation,
                    onStartConversation = handleStartConversation,
                    assigneeName = assignedTo // String
                )
            }

            // --- Botones de Acción ---
            if (ticketStatus != "Cerrado") { // Comparar con String
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { /* No hace nada en preview */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = customColors.success,
                                contentColor = customColors.onSuccess
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Marcar como resuelto", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { /* No hace nada en preview */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Editar ticket", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } // Fin LazyColumn
    } // Fin Scaffold
}


// --- Componentes Helper para la PREVIEW (Usan Strings en lugar de Enums) ---

@Composable
fun TicketInfoCardPreviewHelper(
    category: String,
    priority: String,
    reportedBy: String,
    assignedTo: String,
    createdDate: String,
    description: String,
    customColors: CustomColors
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.Label, // Usar AutoMirrored si aplica
                    label = "Categoría",
                    value = { Text(category, fontWeight = FontWeight.SemiBold) }
                )
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Flag,
                    label = "Prioridad",
                    value = { PriorityBadgePreviewHelper(priority = priority, customColors = customColors, size = "large") }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoItem(
                    icon = Icons.Filled.Person,
                    label = "Reportado por",
                    value = { Text(reportedBy, fontWeight = FontWeight.SemiBold) }
                )
                InfoItem(
                    icon = Icons.Filled.SupportAgent,
                    label = "Técnico asignado",
                    value = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = assignedTo.firstOrNull()?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 16.sp
                                )
                            }
                            Text(assignedTo, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            InfoItem(
                icon = Icons.Filled.Schedule,
                label = "Fecha de creación",
                value = { Text(createdDate, fontWeight = FontWeight.SemiBold) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            InfoItem(
                icon = Icons.Filled.Description,
                label = "Descripción del problema",
                value = { Text(description, lineHeight = 24.sp, style = MaterialTheme.typography.bodyMedium) }
            )
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit // El contenido principal (ej. Text, Badge)
) {
    Column(modifier = modifier) {
        // Fila para Icono y Etiqueta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp), // gap-2
            modifier = Modifier.padding(bottom = 8.dp) // mb-2
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Descripción viene de la etiqueta
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp) // text-lg
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall, // text-xs
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Contenedor para el Valor principal, alineado con el texto de la etiqueta
        Box(modifier = Modifier.padding(start = 28.dp)) { // ml-7 approx (20dp icon + 8dp space)
            // Provee un estilo base para el valor, pero puede ser sobreescrito
            ProvideTextStyle(
                value = MaterialTheme.typography.bodyLarge.copy( // text-base
                    color = MaterialTheme.colorScheme.onSurface
                )
            ) {
                value() // Dibuja el contenido que se pasó (ej. Text, Badge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationCardPreviewHelper(
    messages: List<Map<String, String>>, // Usar Map
    newMessage: String,
    onNewMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    hasConversation: Boolean,
    onStartConversation: () -> Unit,
    assigneeName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    Text("Conversación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                if (hasConversation && messages.isNotEmpty()) {
                    Badge(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ){
                        Text("${messages.size} mensajes", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (!hasConversation) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)){
                        Text("Aún no hay conversación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Inicia una conversación con el técnico asignado para resolver tu problema más rápido",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                    }
                    Button(
                        onClick = onStartConversation,
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Iniciar conversación con $assigneeName", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            messages.forEachIndexed { index, msg -> // Usar forEachIndexed para key
                                MessageBubblePreviewHelper(message = msg, key = index)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = newMessage,
                            onValueChange = onNewMessageChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escribe un mensaje...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                        IconButton(
                            onClick = onSendMessage,
                            enabled = newMessage.isNotBlank(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .size(48.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MessageBubblePreviewHelper(message: Map<String, String>, key: Any) { // Recibe Map y key
    val isUser = message["sender"] == "user"
    val senderName = message["senderName"] ?: ""
    val text = message["text"] ?: ""
    val time = message["time"] ?: ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.75f),
            shape = RoundedCornerShape(16.dp).copy(
                bottomStart = CornerSize(if (!isUser) 2.dp else 16.dp),
                bottomEnd = CornerSize(if (isUser) 2.dp else 16.dp)
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            border = if (!isUser) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = senderName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) LocalContentColor.current.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// --- Componentes Helper StatusChip y PriorityBadge (Usan Strings) ---
@Composable
fun StatusChipPreviewHelper(status: String, customColors: CustomColors, size: String = "small") {
    val textStyle = if (size == "large") MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall
    val paddingValues = if (size == "large") PaddingValues(horizontal = 10.dp, vertical = 5.dp) else PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    val iconSize = if (size == "large") 16.dp else 14.dp

    val (bgColor, contentColor, icon) = when (status.lowercase()) {
        "abierto" -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, null)
        "en progreso", "en-progreso" -> Triple(customColors.warningContainer, customColors.warning, Icons.Default.Schedule)
        "cerrado" -> Triple(customColors.successContainer, customColors.success, Icons.Default.CheckCircleOutline)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, null) // Default
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(iconSize))
        }
        Text(text = status, color = contentColor, style = textStyle, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PriorityBadgePreviewHelper(priority: String, customColors: CustomColors, size: String = "small") {
    val textStyle = if (size == "large") MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall
    val paddingValues = if (size == "large") PaddingValues(horizontal = 8.dp, vertical = 4.dp) else PaddingValues(horizontal = 6.dp, vertical = 3.dp)
    val iconSize = if (size == "large") 16.dp else 14.dp

    val (bgColor, contentColor, icon) = when (priority.lowercase()) {
        "alta" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error, Icons.Default.ErrorOutline)
        "media" -> Triple(customColors.warningContainer, customColors.warning, Icons.Default.WarningAmber)
        "baja" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Icons.Default.KeyboardArrowDown)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, null) // Default
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(iconSize))
        }
        Text(text = priority, color = contentColor, style = textStyle, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, name = "Ticket Detail Screen Preview")
@Composable
fun TicketDetailScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            TicketDetailScreen(
                navController = rememberNavController(),
                ticketId = "PREVIEW-001"
            )
        }
    }
}
