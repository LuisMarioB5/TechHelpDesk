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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.screens.viewmodels.TicketViewModel
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator

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
    modifier: Modifier = Modifier,
    viewModel: TicketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var newMessage by remember { mutableStateOf("") }
    var hasConversation by remember { mutableStateOf(true) }
    val sampleMessages = remember {
        listOf(
            mapOf("sender" to "user", "senderName" to "Luis Rodríguez", "text" to "Hola, tengo problemas con mi impresora, no imprime.", "time" to "10:30 AM"),
            mapOf("sender" to "technician", "senderName" to "Carlos Méndez", "text" to "Entendido. ¿Podrías confirmar si está conectada vía Wi-Fi o cable USB?", "time" to "10:35 AM"),
            mapOf("sender" to "user", "senderName" to "Luis Rodríguez", "text" to "Está conectada por Wi-Fi.", "time" to "10:37 AM"),
            mapOf("sender" to "technician", "senderName" to "Carlos Méndez", "text" to "Perfecto, verifica por favor si la red es la misma del equipo.", "time" to "10:40 AM"),
        )
    }
    var messages by remember { mutableStateOf(if (hasConversation) sampleMessages else emptyList()) }

    val ticket by viewModel.currentTicket.collectAsState()
    val isTechnician by viewModel.isTechnician.collectAsState()

    // Cargar ticket al montar
    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
        viewModel.checkUserRole()
    }

    val handleSendMessage = {
        if (newMessage.isNotBlank()) {
            val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val msg = mapOf(
                "sender" to "user",
                "senderName" to "Luis Rodríguez", // Idealmente vendría del AuthRepository
                "text" to newMessage,
                "time" to currentTime
            )
            messages = messages + msg
            newMessage = ""
            if (!hasConversation) hasConversation = true
        }
    }

    val handleStartConversation = { hasConversation = true }

    val customColors = LocalCustomColors.current

    if (ticket == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val createdDate = remember(ticket) {
        ticket?.createdAt?.toDate()?.let { date ->
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
        } ?: ""
    }

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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Ticket #1234
                            Text(
                                text = stringResource(R.string.title_ticket_number, ticket?.id?.takeLast(8) ?: ticketId),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            StatusChipPreviewHelper(
                                status = ticket?.status ?: "abierto",
                                customColors = customColors,
                                size = "large"
                            )
                        }
                    },
                    bottomContent = {
                        Text(
                            text = ticket?.title ?: stringResource(R.string.loading_ticket),
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
        LazyColumn(
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
            item {
                TicketInfoCardPreviewHelper(
                    category = ticket?.category ?: "",
                    priority = ticket?.priority ?: "",
                    reportedBy = ticket?.createdBy ?: stringResource(R.string.text_unknown_user),
                    assignedTo = ticket?.assignedToName ?: stringResource(R.string.text_unassigned),
                    createdDate = createdDate.ifEmpty { stringResource(R.string.date_unknown) },
                    description = ticket?.description ?: "",
                    customColors = customColors
                )
            }

            item {
                ConversationCardPreviewHelper(
                    messages = messages,
                    newMessage = newMessage,
                    onNewMessageChange = { newMessage = it },
                    onSendMessage = handleSendMessage,
                    hasConversation = hasConversation,
                    onStartConversation = handleStartConversation,
                    assigneeName = ticket?.assignedToName ?: "el técnico"
                )
            }

            if (ticket?.status != "cerrado") {
                item {
                    val currentStatus = ticket?.status?.lowercase() ?: ""

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        if (isTechnician) {
                            if (currentStatus == "abierto") {
                                Button(
                                    onClick = {
                                        viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.EN_PROGRESO)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = customColors.warning,
                                        contentColor = customColors.onWarning
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.btn_mark_in_progress), fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (currentStatus == "en_progreso" || currentStatus == "en progreso") {
                                Button(
                                    onClick = {
                                        viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.RESUELTO)
                                    },
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
                                    Text(stringResource(R.string.btn_mark_resolved), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        if (!isTechnician && currentStatus != "cerrado"){
                            Button(
                                onClick = {
                                    viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.CERRADO)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = customColors.success,
                                    contentColor = customColors.onSuccess
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.btn_mark_closed),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    val displayCategory = when(category.lowercase()) {
        "email" -> stringResource(R.string.cat_email)
        "hardware" -> stringResource(R.string.cat_hardware)
        "software" -> stringResource(R.string.cat_software)
        "network" -> stringResource(R.string.cat_network)
        "permissions" -> stringResource(R.string.cat_permissions)
        "other" -> stringResource(R.string.cat_other)
        else -> category // Fallback si no coincide
    }

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
                    icon = Icons.AutoMirrored.Filled.Label,
                    label = stringResource(R.string.label_category),
                    value = { Text(displayCategory, fontWeight = FontWeight.SemiBold) }
                )
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Flag,
                    label = stringResource(R.string.label_priority),
                    value = { PriorityBadgePreviewHelper(priority = priority, customColors = customColors, size = "large") }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoItem(
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.label_reported_by),
                    value = { Text(reportedBy, fontWeight = FontWeight.SemiBold) }
                )
                InfoItem(
                    icon = Icons.Filled.SupportAgent,
                    label = stringResource(R.string.label_assigned_to),
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
                label = stringResource(R.string.label_created_at),
                value = { Text(createdDate, fontWeight = FontWeight.SemiBold) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            InfoItem(
                icon = Icons.Filled.Description,
                label = stringResource(R.string.label_description_issue),
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
    value: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(modifier = Modifier.padding(start = 28.dp)) {
            ProvideTextStyle(
                value = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            ) {
                value()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationCardPreviewHelper(
    messages: List<Map<String, String>>,
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
                    Text(stringResource(R.string.title_conversation), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                if (hasConversation && messages.isNotEmpty()) {
                    Badge(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ){
                        // Usamos stringResource con formato para el contador
                        Text(stringResource(R.string.badge_messages_count, messages.size), style = MaterialTheme.typography.labelSmall)
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
                        Text(stringResource(R.string.empty_conversation_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = stringResource(R.string.empty_conversation_desc),
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
                        Text(stringResource(R.string.btn_start_conversation, assigneeName), fontWeight = FontWeight.SemiBold)
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
                            messages.forEachIndexed { index, msg ->
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
                            placeholder = { Text(stringResource(R.string.placeholder_chat_input)) },
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
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.cd_send_message))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MessageBubblePreviewHelper(message: Map<String, String>, key: Any) {
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

@Composable
fun StatusChipPreviewHelper(status: String, customColors: CustomColors, size: String = "small") {
    val textStyle = if (size == "large") MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall
    val paddingValues = if (size == "large") PaddingValues(horizontal = 10.dp, vertical = 5.dp) else PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    val iconSize = if (size == "large") 16.dp else 14.dp

    // Mapeo para visualización (traducción)
    val displayStatus = when (status.lowercase()) {
        "abierto" -> stringResource(R.string.status_open)
        "en progreso", "en_progreso" -> stringResource(R.string.status_in_progress)
        "cerrado" -> stringResource(R.string.status_closed)
        "resuelto" -> stringResource(R.string.status_resolved)
        else -> status
    }

    val (bgColor, contentColor, icon) = when (status.lowercase()) {
        "abierto" -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, null)
        "en progreso", "en_progreso" -> Triple(customColors.warningContainer, customColors.warning, Icons.Default.Schedule)
        "cerrado", "resuelto" -> Triple(customColors.successContainer, customColors.success, Icons.Default.CheckCircleOutline)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, null)
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
        Text(text = displayStatus, color = contentColor, style = textStyle, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PriorityBadgePreviewHelper(priority: String, customColors: CustomColors, size: String = "small") {
    val textStyle = if (size == "large") MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall
    val paddingValues = if (size == "large") PaddingValues(horizontal = 8.dp, vertical = 4.dp) else PaddingValues(horizontal = 6.dp, vertical = 3.dp)
    val iconSize = if (size == "large") 16.dp else 14.dp

    // Mapeo para visualización (traducción)
    val displayPriority = when (priority.lowercase()) {
        "alta" -> stringResource(R.string.prio_high)
        "media" -> stringResource(R.string.prio_medium)
        "baja" -> stringResource(R.string.prio_low)
        else -> priority
    }

    val (bgColor, contentColor, icon) = when (priority.lowercase()) {
        "alta" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error, Icons.Default.ErrorOutline)
        "media" -> Triple(customColors.warningContainer, customColors.warning, Icons.Default.WarningAmber)
        "baja" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Icons.Default.KeyboardArrowDown)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, null)
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
        Text(text = displayPriority, color = contentColor, style = textStyle, fontWeight = FontWeight.Medium)
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
