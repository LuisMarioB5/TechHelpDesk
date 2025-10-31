// CreateTicketScreen.kt

package dev.boni.techhelpdesk.ui.screens // O el paquete correcto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Label // Icono para Category Option
import androidx.compose.material.icons.automirrored.filled.Send // Icono Enviar
import androidx.compose.material.icons.filled.* // Importar todos
import androidx.compose.material.icons.outlined.* // Importar todos outlined
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue // Usaremos TextFieldValue para descripción
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.MobileButton // Reutilizamos MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant // Enum de MobileButton
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Datos para el Formulario (Simplificado con Strings) ---
// En una app real, usarías Enums o Data Classes aquí

data class CategoryOption(val value: String, val label: String, val icon: ImageVector, val description: String)
data class PriorityOption(val value: String, val label: String, val icon: ImageVector, val colorKey: String, val description: String) // colorKey para mapear a colores del tema
data class ContactMethodOption(val value: String, val label: String, val icon: ImageVector)

val categories = listOf(
    CategoryOption("email", "Email", Icons.Default.Mail, "Problemas con correo electrónico"),
    CategoryOption("hardware", "Hardware", Icons.Default.Computer, "Equipos y dispositivos"),
    CategoryOption("software", "Software", Icons.Default.Apps, "Aplicaciones y programas"),
    CategoryOption("network", "Red", Icons.Default.Wifi, "Conectividad e internet"),
    CategoryOption("permissions", "Permisos", Icons.Default.Lock, "Accesos y autorizaciones"),
    CategoryOption("other", "Otro", Icons.AutoMirrored.Filled.HelpOutline, "Otros problemas"),
)

val priorities = listOf(
    PriorityOption("baja", "Baja", Icons.Filled.ArrowDownward, "success", "No afecta mi trabajo"),
    PriorityOption("media", "Media", Icons.Filled.Remove, "warning", "Puedo trabajar con limitaciones"), // Remove icon como sustituto de drag_handle
    PriorityOption("alta", "Alta", Icons.Filled.ArrowUpward, "error", "No puedo trabajar"), // ArrowUpward como sustituto
)

val contactMethods = listOf(
    ContactMethodOption("email", "Email", Icons.Default.Mail),
    ContactMethodOption("phone", "Teléfono", Icons.Default.Phone),
    ContactMethodOption("chat", "Chat", Icons.AutoMirrored.Filled.Chat),
)


// --- Pantalla Principal: Crear Ticket ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado del Formulario ---
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") } // Usamos String simple
    var location by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var selectedContactMethod by remember { mutableStateOf("email") } // Valor inicial
    // var attachments by remember { mutableStateOf<List<File>>(emptyList()) } // Manejo de archivos omitido

    // --- Estado de UI y Errores ---
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showSuccess by remember { mutableStateOf(false) }
    var showDraft by remember { mutableStateOf(false) }
    var showOptionalFields by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Para las demoras

    // --- Colores ---
    val customColors = LocalCustomColors.current // Necesario para los colores de prioridad

    // --- Lógica de Validación (Simplificada) ---
    val validateForm: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        if (title.isBlank()) newErrors["title"] = "El título es requerido"
        else if (title.length < 5) newErrors["title"] = "Mínimo 5 caracteres"
        else if (title.length > 100) newErrors["title"] = "Máximo 100 caracteres"

        if (selectedCategory == null) newErrors["category"] = "Selecciona una categoría"
        if (selectedPriority == null) newErrors["priority"] = "Selecciona una prioridad"
        if (description.isBlank()) newErrors["description"] = "La descripción es requerida"
        else if (description.length < 20) newErrors["description"] = "Mínimo 20 caracteres"

        errors = newErrors
        newErrors.isEmpty()
    }

    // --- Lógica de Envío/Guardado (Simulada) ---
    val handleSubmit = {
        if (validateForm()) {
            showSuccess = true
            coroutineScope.launch {
                delay(2000)
                navController.popBackStack() // Volver a la pantalla anterior (TicketsScreen)
                // En una app real, aquí enviarías los datos
            }
        }
        // Scroll to first error? Needs ScrollState and calculation, omitido por simplicidad
    }

    val handleSaveDraft: () -> Unit = {
        showDraft = true
        coroutineScope.launch {
            delay(1500)
            navController.popBackStack() // Volver a la pantalla anterior
            // En una app real, aquí guardarías el borrador
        }
    }

    // --- Lógica para mostrar tiempo estimado ---
    val estimatedTime = remember(selectedPriority) {
        when (selectedPriority) {
            "alta" -> "1-2 horas"
            "media" -> "4-8 horas"
            "baja" -> "24-48 horas"
            else -> "Depende de la prioridad"
        }
    }


    // --- UI Principal ---

    // Mostrar pantallas de Éxito o Borrador si están activas
    if (showSuccess) {
        SuccessScreen(message = "Tu solicitud ha sido enviada correctamente. Un técnico la revisará pronto.")
        return // Detiene la composición aquí
    }
    if (showDraft) {
        DraftScreen(message = "Puedes continuar editando más tarde")
        return // Detiene la composición aquí
    }

    Scaffold(
        topBar = {
            AppHeader(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                title = {
                    Text(
                        "Nuevo ticket",
                        style = MaterialTheme.typography.titleLarge, // text-2xl
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    // Botón Guardar Borrador
                    TextButton(
                        onClick = handleSaveDraft,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.White.copy(alpha = 0.1f), // bg-white/10
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape // rounded-full
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                },
                bottomContent = {
                    Text(
                        "Describe tu problema y te ayudaremos lo antes posible",
                        style = MaterialTheme.typography.bodySmall, // text-sm
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        lineHeight = 18.sp // leading-relaxed approx
                    )
                }
            )
        },
        containerColor = Color.Transparent // Mantenemos el fondo transparente
    ) { innerPadding ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Fondo de la lista
                .padding(bottom = innerPadding.calculateBottomPadding()), // Solo padding inferior
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(), // Padding superior del Scaffold
                // No añadimos padding extra arriba porque el header ya tiene
                bottom = 24.dp // Espacio extra al final
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio entre secciones (space-y-6)
        ) {
            // --- Consejo Info Box ---
            item {
                Surface( // Simula el div con fondo y padding
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = 10.dp), // -mt-4 approx
                    shape = RoundedCornerShape(16.dp), // rounded-2xl
                    color = MaterialTheme.colorScheme.tertiaryContainer, // Usamos un color del tema
                    shadowElevation = 2.dp // shadow-sm
                ) {
                    Row(modifier = Modifier.padding(16.dp)) { // p-4
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(end = 12.dp) // gap-3
                        )
                        Column {
                            Text("Consejo", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(bottom = 4.dp))
                            Text("Proporciona todos los detalles posibles para que podamos resolver tu problema más rápido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha=0.8f), lineHeight = 16.sp)
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
                    Text("Paso 1 de 1 • Campos obligatorios marcados con *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // --- Title Field ---
            item {
                FormField(label = "Título del ticket", isRequired = true, error = errors["title"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            if (it.length <= 100) { // Limitar caracteres
                                title = it
                                errors = errors - "title" // Limpiar error al escribir
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: No puedo acceder a mi correo") },
                        leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                        isError = errors.containsKey("title"),
                        supportingText = {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(errors["title"] ?: "") // Muestra error si existe
                                Text("${title.length}/100") // Contador
                            }
                        },
                        singleLine = true
                    )
                }
            }

            // --- Category Selection ---
            item {
                FormField(label = "Categoría", isRequired = true, error = errors["category"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Usamos FlowRow para que se ajuste si no caben 3
                    FlowRow( // Reemplaza grid grid-cols-3
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally), // Centrado y gap
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
                                // Aproximamos el tamaño basado en grid-cols-3
                                modifier = Modifier.widthIn(min = 90.dp) //.weight(1f / 3f) no funciona bien con FlowRow
                            )
                        }
                    }
                    // Mostrar descripción seleccionada
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
                FormField(label = "Prioridad", isRequired = true, error = errors["priority"], helperText = "Selecciona según cómo afecta tu trabajo", modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // space-y-3
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
                FormField(label = "Descripción del problema", isRequired = true, error = errors["description"], modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column {
                        Text(
                            "Incluye: qué pasó, cuándo ocurrió, qué intentaste hacer y cualquier mensaje de error",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp),
                            lineHeight = 16.sp
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                if (it.length <= 500) { // Limitar caracteres
                                    description = it
                                    errors = errors - "description"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(150.dp), // rows = 6 approx
                            placeholder = { Text("Ejemplo: Desde esta mañana no puedo abrir mi correo...") },
                            isError = errors.containsKey("description"),
                            supportingText = {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(errors["description"] ?: "") // Muestra error
                                    Text("${description.length}/500") // Contador
                                }
                            },
                            shape = RoundedCornerShape(12.dp) // rounded-xl
                        )
                    }
                }
            }

            // --- Attachments ---
            item {
                FormField(label = "Adjuntar archivos (opcional)", helperText = "Las capturas de pantalla ayudan a resolver el problema más rápido", modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { // gap-3
                        AttachmentButton(
                            text = "Cámara",
                            icon = Icons.Filled.PhotoCamera,
                            onClick = { /* Lógica cámara */ },
                            modifier = Modifier.weight(1f)
                        )
                        AttachmentButton(
                            text = "Galería",
                            icon = Icons.Filled.Image,
                            onClick = { /* Lógica galería */ },
                            modifier = Modifier.weight(1f)
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
                    // Separador
                    Surface( // Botón para expandir
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
                                    Text("Información adicional", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                                    Text("Opcional pero útil", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(
                                if (showOptionalFields) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (showOptionalFields) "Ocultar" else "Mostrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Campos opcionales (animados)
                    AnimatedVisibility(visible = showOptionalFields) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Ubicación") },
                                placeholder = { Text("Ej: Edificio A, Piso 3...") },
                                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null)},
                                supportingText = { Text("Dónde te encuentras físicamente")},
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Departamento") },
                                placeholder = { Text("Ej: Recursos Humanos") },
                                leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null)},
                                supportingText = { Text("Tu área o departamento")},
                                singleLine = true
                            )
                            FormField(label = "Método de contacto preferido") {
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
                Surface( // Tarjeta de info
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp), // rounded-2xl
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ){
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end=12.dp))
                        Column {
                            Text("Tiempo de respuesta estimado", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom=4.dp))
                            Text(estimatedTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        }
                    }
                }
            }

            // --- Submit/Cancel Buttons ---
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), // pt-4 y padding h
                    verticalArrangement = Arrangement.spacedBy(12.dp) // space-y-3
                ) {
                    MobileButton(
                        onClick = handleSubmit,
                        variant = MobileButtonVariant.FILLED,
                        fullWidth = true,
                        // El botón se deshabilita si faltan campos requeridos
                        enabled = title.isNotBlank() && selectedCategory != null && selectedPriority != null && description.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Enviar ticket", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                    MobileButton(
                        onClick = { navController.popBackStack() }, // Botón cancelar vuelve atrás
                        variant = MobileButtonVariant.OUTLINED,
                        fullWidth = true,
                        size = dev.boni.techhelpdesk.ui.components.MobileButtonSize.SMALL // h-12 text-sm approx
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        } // Fin LazyColumn
    } // Fin Scaffold
}


// --- Componentes Helper para la UI del Formulario ---

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
        modifier = modifier.height(56.dp), // h-14
        shape = RoundedCornerShape(12.dp), // rounded-xl
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
                modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), // Usamos primary container como sustituto de success container
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) // Usamos primary como sustituto de success
            }
            Spacer(Modifier.height(16.dp))
            Text("¡Ticket creado!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
            Text("Borrador guardado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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