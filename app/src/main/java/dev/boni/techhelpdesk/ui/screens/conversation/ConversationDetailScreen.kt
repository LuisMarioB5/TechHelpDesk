package dev.boni.techhelpdesk.ui.screens.conversation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Definiciones de Datos (Visibilidad corregida) ---
data class Message(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" o "agent"
    val text: String,
    val timestamp: Date
)

val technicianData = mapOf(
    "1" to mapOf("name" to "Carlos Méndez", "avatar" to "C", "ticketId" to "T-1234"),
    "2" to mapOf("name" to "Ana García", "avatar" to "A", "ticketId" to null),
    "3" to mapOf("name" to "Luis Torres", "avatar" to "L", "ticketId" to "T-1220"),
)
val quickReplies = listOf(
    "Problema con equipo" to Icons.Default.Computer,
    "Conexión de red" to Icons.Default.Wifi,
    "Acceso a sistema" to Icons.Default.Lock,
    "Impresora" to Icons.Default.Print,
)
fun formatTime(date: Date): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
}

// --- Pantalla de Detalle de Conversación ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    navController: NavController,
    chatId: String,
    modifier: Modifier = Modifier
) {
    val technician = technicianData[chatId] ?: mapOf("name" to "Técnico", "avatar" to "T", "ticketId" to null)
    val technicianName = technician["name"] ?: "Técnico"
    val technicianAvatar = technician["avatar"] ?: "T"
    val ticketId = technician["ticketId"]

    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var messages by remember {
        mutableStateOf(listOf(
            Message(sender = "agent", text = "Hola, soy $technicianName del equipo de soporte técnico. ¿En qué puedo ayudarte hoy?", timestamp = Date(System.currentTimeMillis() - 120000))
        ))
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // --- CAMBIO: Lógica de 'return' corregida ---
    val handleSendMessage = {
        if (inputText.isNotBlank()) { // Solo ejecutar si no está en blanco
            val newMessage = Message(
                id = System.currentTimeMillis().toString(),
                sender = "user",
                text = inputText,
                timestamp = Date()
            )
            messages = messages + newMessage
            inputText = ""

            isTyping = true
            coroutineScope.launch {
                delay(2000)
                val agentResponse = Message(
                    id = (System.currentTimeMillis() + 1).toString(),
                    sender = "agent",
                    text = "Entiendo tu consulta. Déjame revisar eso para ti. ¿Podrías proporcionarme más detalles?",
                    timestamp = Date()
                )
                messages = messages + agentResponse
                isTyping = false
            }
        }
        // No hay 'return' explícito aquí
    }


    LaunchedEffect(messages, isTyping) {
        if (messages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(technicianAvatar, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(technicianName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF66BB6A)))
                            Text("En línea", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    IconButton(
                        onClick = { /* Lógica "Más Opciones" */ },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (ticketId != null) {
                Surface(
                    // --- CAMBIO: Ruta de navegación corregida ---
                    onClick = { navController.navigate("/ticket/detail/$ticketId") }, // Usar /ticketdetail/
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            "Relacionado con: Ticket #$ticketId",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimatedVisibility(visible = isTyping) {
                        TypingIndicator(avatar = technicianAvatar)
                    }
                }
                items(messages.reversed(), key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        avatar = technicianAvatar
                    )
                }
                item {
                    DateSeparator("Hoy")
                }
            }

            AnimatedVisibility(visible = messages.size == 1) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickReplies) { (text, icon) ->
                        QuickReplyChip(
                            text = text,
                            icon = icon,
                            onClick = { inputText = text }
                        )
                    }
                }
            }

            MessageInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = handleSendMessage
            )
        }
    }
}


// --- Componentes Helper (Sin cambios de visibilidad) ---

@Composable
fun DateSeparator(date: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message, avatar: String) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier.widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.75f),
                // --- CAMBIO: CornerSize añadido ---
                shape = RoundedCornerShape(20.dp).copy(
                    bottomStart = CornerSize(if (!isUser) 4.dp else 20.dp),
                    bottomEnd = CornerSize(if (isUser) 4.dp else 20.dp)
                ),
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                shadowElevation = if (!isUser) 1.dp else 0.dp
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    lineHeight = 20.sp
                )
            }
            Text(
                text = formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator(avatar: String) {
    // --- CAMBIO: Animaciones importadas correctamente ---
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha1 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.2f at 0; 1f at 300; 0.2f at 600; 0.2f at 1200 }, repeatMode = RepeatMode.Restart), label = "alpha1")
    val alpha2 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.2f at 200; 1f at 500; 0.2f at 800; 0.2f at 1200 }, repeatMode = RepeatMode.Restart), label = "alpha2")
    val alpha3 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.2f at 400; 1f at 700; 0.2f at 1000; 0.2f at 1200 }, repeatMode = RepeatMode.Restart), label = "alpha3")

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(avatar, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        Surface(
            shape = RoundedCornerShape(20.dp).copy(bottomStart = CornerSize(4.dp)), // <-- CornerSize
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha1)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha2)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha3)))
            }
        }
    }
}

@Composable
fun QuickReplyChip(text: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { /* Lógica adjuntar */ },
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar archivo")
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                trailingIcon = {
                    IconButton(onClick = { /* Lógica Emoji */ }) {
                        Icon(Icons.Filled.SentimentSatisfied, contentDescription = "Emoji")
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
            )

            val isEnabled = value.isNotBlank()
            IconButton(
                onClick = onSend,
                enabled = isEnabled,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ConversationDetailScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            ConversationDetailScreen(navController = navController, chatId = "1")
        }
    }
}

@Preview(showBackground = true, name = "Chat Sin Contexto ni Replies")
@Composable
fun ConversationDetailScreenNoContextPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            ConversationDetailScreen(navController = navController, chatId = "2")
        }
    }
}