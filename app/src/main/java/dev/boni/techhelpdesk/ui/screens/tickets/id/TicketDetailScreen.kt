package dev.boni.techhelpdesk.ui.screens.tickets.id

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.boni.techhelpdesk.data.model.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    navController: NavController,
    ticketId: String,
    modifier: Modifier = Modifier,
    viewModel: TicketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val ticket by viewModel.currentTicket.collectAsState()
    val isTechnician by viewModel.isTechnician.collectAsState()

    val currentUserId = remember { Firebase.auth.currentUser?.uid ?: "" }

    var newMessage by remember { mutableStateOf("") }

    // Estado local para saber si el usuario decidió iniciar la conversación manualmente
    var localConversationStarted by remember { mutableStateOf(false) }

    // CARGA DE DATOS AL ENTRAR
    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
        viewModel.loadMessages(ticketId)
        viewModel.checkRoleAndLoadTickets()
    }

    val customColors = LocalCustomColors.current

    if (ticket == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                end = 16.dp,
                bottom = 16.dp
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

            // SECCIÓN DE CHAT
            item {
                ConversationCardPreviewHelper(
                    messages = messages,
                    newMessage = newMessage,
                    onNewMessageChange = { newMessage = it },
                    onSendMessage = {
                        viewModel.sendMessage(newMessage)
                        newMessage = ""
                    },
                    currentUserId = currentUserId,
                    assigneeName = ticket?.assignedToName ?: "el técnico",
                    isChatActive = messages.isNotEmpty() || localConversationStarted,
                    onStartChat = { localConversationStarted = true }
                )
            }

            // BOTONES DE ACCIÓN
            if (ticket?.status != "CERRADO" && ticket?.status != "cerrado") {
                item {
                    val currentStatus = ticket?.status?.uppercase() ?: ""

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (isTechnician) {
                            if (currentStatus == "ABIERTO") {
                                ActionButton(
                                    text = stringResource(R.string.btn_mark_in_progress),
                                    icon = Icons.Filled.Schedule,
                                    color = customColors.warning,
                                    contentColor = customColors.onWarning
                                ) {
                                    viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.EN_PROGRESO)
                                }
                            }

                            if (currentStatus == "EN_PROGRESO" || currentStatus == "EN PROGRESO") {
                                ActionButton(
                                    text = stringResource(R.string.btn_mark_resolved),
                                    icon = Icons.Filled.CheckCircle,
                                    color = customColors.success,
                                    contentColor = customColors.onSuccess
                                ) {
                                    viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.RESUELTO)
                                }
                            }
                        }

                        if (!isTechnician && currentStatus == "RESUELTO") {
                            ActionButton(
                                text = stringResource(R.string.btn_mark_closed),
                                icon = Icons.Filled.Lock,
                                color = customColors.success,
                                contentColor = customColors.onSuccess
                            ) {
                                viewModel.updateTicketStatus(dev.boni.techhelpdesk.data.model.TicketStatus.CERRADO)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, color: Color, contentColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.SemiBold)
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
    messages: List<ChatMessage>,
    newMessage: String,
    onNewMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    currentUserId: String,
    assigneeName: String,
    isChatActive: Boolean,
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column {
            // Header del Chat
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
                if (messages.isNotEmpty()) {
                    Badge(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ){
                        Text("${messages.size}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (!isChatActive) {
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
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
                        onClick = onStartChat,
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
                            .heightIn(min = 200.dp, max = 400.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        if (messages.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                reverseLayout = true
                            ) {
                                itemsIndexed(messages.reversed()) { index, msg ->
                                    MessageBubblePreviewHelper(
                                        message = msg,
                                        isUser = msg.senderId == currentUserId
                                    )
                                }
                            }
                        }
                    }
                    ChatInputArea(newMessage, onNewMessageChange, onSendMessage)
                }
            }
        }
    }
}

@Composable
fun ChatInputArea(
    newMessage: String,
    onNewMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
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
            singleLine = true
        )
        IconButton(
            onClick = onSendMessage,
            enabled = newMessage.isNotBlank(),
            modifier = Modifier
                .clip(CircleShape)
                .background(if (newMessage.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .size(48.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.cd_send_message),
                tint = if (newMessage.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MessageBubblePreviewHelper(message: ChatMessage, isUser: Boolean) {
    val time = remember(message.timestamp) {
        message.timestamp?.toDate()?.let {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(it)
        } ?: "..."
    }

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
                if (!isUser) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// ... (Resto de Helpers como PriorityBadgePreviewHelper y StatusChipPreviewHelper que ya tenías)
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