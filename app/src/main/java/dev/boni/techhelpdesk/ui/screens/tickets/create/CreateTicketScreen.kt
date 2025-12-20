package dev.boni.techhelpdesk.ui.screens.tickets.create

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonSize
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
import dev.boni.techhelpdesk.ui.screens.viewmodels.TicketViewModel
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CategoryOption(val value: String, val label: String, val icon: ImageVector, val description: String)
data class PriorityOption(val value: String, val label: String, val icon: ImageVector, val colorKey: String, val description: String) // colorKey para mapear a colores del tema
data class ContactMethodOption(val value: String, val label: String, val icon: ImageVector)

// --- Pantalla Principal: Crear Ticket ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // --- Colores ---
    val customColors = LocalCustomColors.current

    val categories = listOf(
        CategoryOption("email", stringResource(R.string.cat_email), Icons.Default.Mail, stringResource(R.string.cat_desc_email)),
        CategoryOption("hardware", stringResource(R.string.cat_hardware), Icons.Default.Computer, stringResource(R.string.cat_desc_hardware)),
        CategoryOption("software", stringResource(R.string.cat_software), Icons.Default.Apps, stringResource(R.string.cat_desc_software)),
        CategoryOption("network", stringResource(R.string.cat_network), Icons.Default.Wifi, stringResource(R.string.cat_desc_network)),
        CategoryOption("permissions", stringResource(R.string.cat_permissions), Icons.Default.Lock, stringResource(R.string.cat_desc_permissions)),
        CategoryOption("other", stringResource(R.string.cat_other), Icons.AutoMirrored.Filled.HelpOutline, stringResource(R.string.cat_desc_other)),
    )

    val priorities = listOf(
        PriorityOption("baja", stringResource(R.string.prio_low), Icons.Filled.ArrowDownward, "success", stringResource(R.string.prio_desc_low)),
        PriorityOption("media", stringResource(R.string.prio_medium), Icons.Filled.Remove, "warning", stringResource(R.string.prio_desc_medium)),
        PriorityOption("alta", stringResource(R.string.prio_high), Icons.Filled.ArrowUpward, "error", stringResource(R.string.prio_desc_high)),
    )

    val contactMethods = listOf(
        ContactMethodOption("email", stringResource(R.string.contact_email), Icons.Default.Mail),
        ContactMethodOption("phone", stringResource(R.string.contact_phone), Icons.Default.Phone),
        ContactMethodOption("chat", stringResource(R.string.contact_chat), Icons.AutoMirrored.Filled.Chat),
    )

    // --- Estado del Formulario ---
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var selectedContactMethod by remember { mutableStateOf("email") }
    // var attachments by remember { mutableStateOf<List<File>>(emptyList()) } // Manejo de archivos omitido

    // --- Estado de UI y Errores ---
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showSuccess by remember { mutableStateOf(false) }
    var showDraft by remember { mutableStateOf(false) }
    var showOptionalFields by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // --- Lógica de Validación ---
    val validateForm: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        if (title.isBlank()) newErrors["title"] = context.getString(R.string.val_title_required)
        else if (title.length < 5) newErrors["title"] = context.getString(R.string.val_title_min)
        else if (title.length > 100) newErrors["title"] = context.getString(R.string.val_title_max)

        if (selectedCategory == null) newErrors["category"] = context.getString(R.string.val_category_required)
        if (selectedPriority == null) newErrors["priority"] = context.getString(R.string.val_priority_required)
        if (description.isBlank()) newErrors["description"] = context.getString(R.string.val_desc_required)
        else if (description.length < 20) newErrors["description"] = context.getString(R.string.val_desc_min)

        errors = newErrors
        newErrors.isEmpty()
    }

    val viewModel = viewModel<TicketViewModel>()

    val handleSubmit = {
        if (validateForm()) {
            coroutineScope.launch {
                val result = viewModel.createTicket(
                    title = title,
                    description = description,
                    category = selectedCategory ?: "",
                    priority = selectedPriority ?: "",
                    location = location,
                    department = department,
                    contactMethod = selectedContactMethod
                )

                if (result.isSuccess) {
                    showSuccess = true
                    delay(2000)
                    navController.popBackStack()
                } else {
                    errors = errors + ("submit" to "Error: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    val handleSaveDraft: () -> Unit = {
        showDraft = true
        coroutineScope.launch {
            delay(1500)
            navController.popBackStack()
            // aquí debería guardarse el borrador
        }
    }

    // --- Lógica para mostrar tiempo estimado ---
    val estimatedTime = remember(selectedPriority) {
        when (selectedPriority) {
            "alta" -> "1-2 h"
            "media" -> "4-8 h"
            "baja" -> "24-48 h"
            else -> "-"
        }
    }


    // --- UI Principal ---

    if (showSuccess) {
        SuccessScreen(message = stringResource(R.string.success_ticket_message))
        return
    }
    if (showDraft) {
        DraftScreen(message = stringResource(R.string.draft_ticket_message))
        return
    }

    Scaffold(
        topBar = {
            AppHeader(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.create_ticket_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    TextButton(
                        onClick = handleSaveDraft,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.btn_save_draft), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                },
                bottomContent = {
                    Text(
                        stringResource(R.string.create_ticket_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Consejo Info Box ---
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shadowElevation = 2.dp
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(stringResource(R.string.tip_title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(bottom = 4.dp))
                            Text(stringResource(R.string.tip_description), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha=0.8f), lineHeight = 16.sp)
                        }
                    }
                }
            }

            // --- Indicador de Paso ---
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.step_indicator), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // --- Title Field ---
            item {
                FormField(label = stringResource(R.string.label_ticket_title), isRequired = true, error = errors["title"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            if (it.length <= 100) {
                                title = it
                                errors = errors - "title"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.placeholder_ticket_title)) },
                        leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                        isError = errors.containsKey("title"),
                        supportingText = {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(errors["title"] ?: "")
                                Text("${title.length}/100")
                            }
                        },
                        singleLine = true
                    )
                }
            }

            // --- Category Selection ---
            item {
                FormField(label = stringResource(R.string.label_category), isRequired = true, error = errors["category"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat.value
                            SelectableCard(
                                text = cat.label,
                                icon = cat.icon,
                                isSelected = isSelected,
                                onClick = {
                                    selectedCategory = cat.value
                                    errors = errors - "category"
                                },
                                description = cat.description,
                                modifier = Modifier.widthIn(min = 90.dp)
                            )
                        }
                    }
                    val selectedDesc = categories.find { it.value == selectedCategory }?.description
                    if (!selectedDesc.isNullOrBlank()) {
                        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = customColors.success, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(selectedDesc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // --- Priority Selection ---
            item {
                FormField(label = stringResource(R.string.label_priority), isRequired = true, error = errors["priority"], helperText = stringResource(R.string.helper_priority), modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        priorities.forEach { priority ->
                            val isSelected = selectedPriority == priority.value
                            SelectablePriorityRow(
                                option = priority,
                                isSelected = isSelected,
                                onClick = {
                                    selectedPriority = priority.value
                                    errors = errors - "priority"
                                },
                                customColors = customColors
                            )
                        }
                    }
                }
            }

            // --- Description ---
            item {
                FormField(label = stringResource(R.string.label_description), isRequired = true, error = errors["description"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column {
                        Text(
                            stringResource(R.string.helper_description_detail),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp),
                            lineHeight = 16.sp
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                if (it.length <= 500) {
                                    description = it
                                    errors = errors - "description"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            placeholder = { Text(stringResource(R.string.placeholder_description)) },
                            isError = errors.containsKey("description"),
                            supportingText = {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(errors["description"] ?: "")
                                    Text("${description.length}/500")
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // --- Attachments ---
            item {
                FormField(label = stringResource(R.string.label_attachments), helperText = stringResource(R.string.helper_attachments), modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AttachmentButton(
                            text = stringResource(R.string.btn_camera),
                            icon = Icons.Filled.PhotoCamera,
                            onClick = {
                                Toast.makeText(context, "Funcionalidad habilitada en la versión Pro", Toast.LENGTH_SHORT).show()
                            },modifier = Modifier.weight(1f)
                        )
                        AttachmentButton(
                            text = stringResource(R.string.btn_gallery),
                            icon = Icons.Filled.Image,
                            onClick = {
                                Toast.makeText(context, "Funcionalidad habilitada en la versión Pro", Toast.LENGTH_SHORT).show()
                            },modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- Optional Fields ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HorizontalDivider(
                        Modifier.padding(vertical = 16.dp),
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )
                    Surface(
                        onClick = { showOptionalFields = !showOptionalFields },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Filled.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(stringResource(R.string.label_additional_info), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                                    Text(stringResource(R.string.sublabel_additional_info), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(
                                if (showOptionalFields) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (showOptionalFields) stringResource(R.string.cd_collapse_options) else stringResource(R.string.cd_expand_options),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AnimatedVisibility(visible = showOptionalFields) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.label_location)) },
                                placeholder = { Text(stringResource(R.string.placeholder_location)) },
                                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null)},
                                supportingText = { Text(stringResource(R.string.helper_location))},
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.label_department)) },
                                placeholder = { Text(stringResource(R.string.placeholder_department)) },
                                leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null)},
                                supportingText = { Text(stringResource(R.string.helper_department))},
                                singleLine = true
                            )
                            FormField(label = stringResource(R.string.label_contact_method)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    contactMethods.forEach { method ->
                                        val isSelected = selectedContactMethod == method.value
                                        SelectableChip(
                                            text = method.label,
                                            icon = method.icon,
                                            isSelected = isSelected,
                                            onClick = { selectedContactMethod = method.value },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Estimated Time ---
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ){
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end=12.dp))
                        Column {
                            Text(stringResource(R.string.label_estimated_time), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom=4.dp))
                            Text(estimatedTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        }
                    }
                }
            }

            // --- Submit/Cancel Buttons ---
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MobileButton(
                        onClick = handleSubmit,
                        variant = MobileButtonVariant.FILLED,
                        fullWidth = true,
                        enabled = title.isNotBlank() && selectedCategory != null && selectedPriority != null && description.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_send_ticket), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                    MobileButton(
                        onClick = { navController.popBackStack() },
                        variant = MobileButtonVariant.OUTLINED,
                        fullWidth = true,
                        size = dev.boni.techhelpdesk.ui.components.MobileButtonSize.SMALL
                    ) {
                        Text(stringResource(R.string.btn_cancel_ticket))
                    }
                }
            }
        }
    }
}


// Wrapper para Label, HelperText y ErrorText
@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    helperText: String? = null,
    error: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = if (helperText != null) 4.dp else 12.dp) ){
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRequired) {
                Text(" *", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }
        }
        if (helperText != null) {
            Text(
                text = helperText,
                style = MaterialTheme.typography.labelSmall, // text-xs
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp), // mb-3
                lineHeight = 16.sp // leading-relaxed
            )
        }
        content() // El input real (TextField, Row de Chips, etc.)
        if (error != null) {
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// Tarjeta seleccionable para Categoría
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableCard(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp), // rounded-xl
        color = containerColor,
        border = BorderStroke(if(isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 3.dp else 0.dp, // shadow-md
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // p-4
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically) // gap-2 y centrado
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(28.dp)) // size="lg"
            Text(text = text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = labelColor, textAlign = TextAlign.Center)
        }
    }
}

// Fila seleccionable para Prioridad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectablePriorityRow(
    option: PriorityOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    customColors: CustomColors,
    modifier: Modifier = Modifier
) {
    val themeColors = MaterialTheme.colorScheme
    val (selectedBg, selectedContent, selectedBorder) = when (option.colorKey) {
        "error" -> Triple(themeColors.errorContainer, themeColors.onErrorContainer, themeColors.error)
        "warning" -> Triple(customColors.warningContainer, customColors.onWarningContainer, customColors.warning)
        "success" -> Triple(customColors.successContainer, customColors.onSuccessContainer, customColors.success)
        else -> Triple(themeColors.primaryContainer, themeColors.primary, themeColors.primary) // Default or fallback
    }
    val (iconBg, iconContent) = when {
        isSelected && option.colorKey == "error" -> Pair(themeColors.error, themeColors.onError)
        isSelected && option.colorKey == "warning" -> Pair(customColors.warning, customColors.onWarning)
        isSelected && option.colorKey == "success" -> Pair(customColors.success, customColors.onSuccess)
        else -> Pair(themeColors.surfaceVariant, themeColors.onSurfaceVariant)
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // rounded-xl
        color = if (isSelected) selectedBg else themeColors.surface,
        contentColor = if (isSelected) selectedContent else themeColors.onSurfaceVariant,
        border = BorderStroke(if(isSelected) 2.dp else 1.dp, if (isSelected) selectedBorder else themeColors.outlineVariant),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // p-4
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp) // gap-3
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center
            ){
                Icon(option.icon, contentDescription = null, tint = iconContent)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(option.label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                Text(option.description, style = MaterialTheme.typography.labelSmall, color = LocalContentColor.current.copy(alpha=0.8f))
            }
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Seleccionado", tint = selectedContent)
            }
        }
    }
}

// Botón para adjuntar archivos
@Composable
fun AttachmentButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) // border-dashed simulado con sólido
    ){
        Icon(icon, contentDescription = null, tint=MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
    }
}

// Chip seleccionable para Método de Contacto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp), // h-14
        shape = RoundedCornerShape(12.dp), // rounded-xl
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal=12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) // size="sm"
            Spacer(Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.labelMedium) // text-sm
        }
    }
}


// --- Pantallas de Estado ---
@Composable
fun SuccessScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.success_ticket_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DraftScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.draft_ticket_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


// --- Preview Principal ---
@Preview(showBackground = true)
@Composable
fun CreateTicketScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            CreateTicketScreen(navController = navController)
        }
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun SuccessScreenPreview(){
    TechHelpDeskTheme {
        SuccessScreen("Tu solicitud ha sido enviada correctamente. Un técnico la revisará pronto.")
    }
}

@Preview(showBackground = true, name = "Draft State")
@Composable
fun DraftScreenPreview(){
    TechHelpDeskTheme {
        DraftScreen("Puedes continuar editando más tarde")
    }
}