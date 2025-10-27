package dev.boni.techhelpdesk.ui.screens.tickets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Apps // Category Software
import androidx.compose.material.icons.filled.ArrowDownward // Priority Low
import androidx.compose.material.icons.filled.Category // Category All
import androidx.compose.material.icons.filled.CheckCircle // Status Closed
import androidx.compose.material.icons.filled.CheckCircleOutline // Usado en StatusChip
import androidx.compose.material.icons.filled.DragHandle // Priority Medium (simple line)
import androidx.compose.material.icons.filled.Email // Category Email
import androidx.compose.material.icons.filled.ErrorOutline // Para Alta prioridad
import androidx.compose.material.icons.filled.Inbox // Para "No tickets"
import androidx.compose.material.icons.filled.KeyboardArrowDown // Para Baja prioridad
import androidx.compose.material.icons.filled.Lock // Category Permissions
import androidx.compose.material.icons.filled.MoreHoriz // Category Other
import androidx.compose.material.icons.filled.Pending // Status In Progress
import androidx.compose.material.icons.filled.PriorityHigh // Priority High
import androidx.compose.material.icons.filled.RadioButtonUnchecked // Status Open
import androidx.compose.material.icons.filled.Schedule // Usado en StatusChip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsSuggest // Category Hardware (alternative)
import androidx.compose.material.icons.filled.Tune // Filter button icon
import androidx.compose.material.icons.filled.WarningAmber // Para Media prioridad (sustituto)
import androidx.compose.material.icons.filled.Wifi // Category Network
// import androidx.compose.material3.CircularProgressIndicator // No se usa
// import androidx.compose.material3.RadioButton // No se usa
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button // For Clear Filters button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors // Usado en Preview
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import java.util.Locale

enum class TicketCategory(val displayName: String, val icon: ImageVector) {
    EMAIL("Email", Icons.Default.Email),
    HARDWARE("Hardware", Icons.Default.SettingsSuggest),
    SOFTWARE("Software", Icons.Default.Apps),
    RED("Red", Icons.Default.Wifi),
    PERMISOS("Permisos", Icons.Default.Lock),
    OTRO("Otro", Icons.Default.MoreHoriz)
}

enum class TicketStatus(val displayName: String, val icon: ImageVector) {
    ABIERTO("Abierto", Icons.Default.RadioButtonUnchecked),
    EN_PROGRESO("En progreso", Icons.Default.Pending),
    CERRADO("Cerrado", Icons.Default.CheckCircle);

    companion object {
        fun fromRouteString(routeString: String?): TicketStatus? {
            return when (routeString?.lowercase(Locale.ROOT)?.replace("-","_")) {
                "abierto" -> ABIERTO
                "en_progreso" -> EN_PROGRESO // Ahora coincide
                "cerrado" -> CERRADO
                else -> null // Si no viene nada o no coincide
            }
        }
    }
}

enum class TicketPriority(val displayName: String, val icon: ImageVector) {
    ALTA("Alta", Icons.Default.PriorityHigh),
    MEDIA("Media", Icons.Default.DragHandle), // Using simple line icon
    BAJA("Baja", Icons.Default.ArrowDownward)
}

data class Ticket(
    val id: String,
    val title: String,
    val category: TicketCategory, // Usando Enum
    val status: TicketStatus,
    val priority: TicketPriority,
    val assignedTo: String,
    val date: String
)

// --- Datos de Ejemplo Actualizados ---
val sampleTickets = listOf(
    Ticket("T-2025-001", "Correo no envía adjuntos", TicketCategory.EMAIL, TicketStatus.ABIERTO, TicketPriority.ALTA, "CM", "Hace 2h"),
    Ticket("T-2025-002", "Impresora no responde", TicketCategory.HARDWARE, TicketStatus.EN_PROGRESO, TicketPriority.MEDIA, "AG", "Hace 5h"),
    Ticket("T-2025-003", "Acceso a carpeta", TicketCategory.PERMISOS, TicketStatus.ABIERTO, TicketPriority.BAJA, "LT", "Hace 1d"),
    Ticket("T-2025-004", "Actualización software", TicketCategory.SOFTWARE, TicketStatus.CERRADO, TicketPriority.MEDIA, "ML", "Hace 2d"),
    Ticket("T-2025-005", "VPN no conecta", TicketCategory.RED, TicketStatus.EN_PROGRESO, TicketPriority.ALTA, "CM", "Hace 3h"),
    Ticket("T-2025-006", "Configuración correo móvil", TicketCategory.EMAIL, TicketStatus.ABIERTO, TicketPriority.BAJA, "AG", "Hace 4h"),
    Ticket("T-2025-007", "Teclado dañado", TicketCategory.HARDWARE, TicketStatus.ABIERTO, TicketPriority.MEDIA, "LT", "Hace 6h"),
)

// --- Opciones de Filtro ---
val statusFilterOptions = listOf(null) + TicketStatus.entries // null es "Todos"
val priorityFilterOptions = listOf(null) + TicketPriority.entries // null es "Todas"
val categoryFilterOptions = listOf(null) + TicketCategory.entries // null es "Todas"


// --- Pantalla Principal de Tickets ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    navController: NavController,
    initialFilterStatus: String? = null,
) {
    var searchQuery by remember { mutableStateOf("") }
    val initialStatusEnum = TicketStatus.fromRouteString(initialFilterStatus)
    var selectedStatus by remember { mutableStateOf(initialStatusEnum) }
    var selectedPriority by remember { mutableStateOf<TicketPriority?>(null) }
    var selectedCategory by remember { mutableStateOf<TicketCategory?>(null) }
    var showFilters by remember { mutableStateOf(false) } // Estado para mostrar/ocultar filtros

    val filteredTickets by remember {
        derivedStateOf {
            sampleTickets.filter { ticket ->
                val matchesSearch = searchQuery.isBlank() ||
                        ticket.title.contains(searchQuery, ignoreCase = true) ||
                        ticket.category.displayName.contains(searchQuery, ignoreCase = true) || // Buscar por nombre de categoría
                        ticket.id.contains(searchQuery, ignoreCase = true)
                val matchesStatus = selectedStatus == null || ticket.status == selectedStatus
                val matchesPriority = selectedPriority == null || ticket.priority == selectedPriority
                val matchesCategory = selectedCategory == null || ticket.category == selectedCategory
                matchesSearch && matchesStatus && matchesPriority && matchesCategory
            }
        }
    }

    val activeFiltersCount by remember {
        derivedStateOf {
            listOfNotNull(selectedStatus, selectedPriority, selectedCategory).size
        }
    }

    val clearAllFilters = {
        selectedStatus = null
        selectedPriority = null
        selectedCategory = null
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = {
                    Text(
                        text = "Mis Tickets",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                // --- CAMBIO: Botón de filtro añadido a las acciones ---
                actions = {
                    BadgedBox(
                        badge = {
                            if (activeFiltersCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text("$activeFiltersCount")
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { showFilters = !showFilters },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Filtros"
                            )
                        }
                    }
                },
                bottomContent = {
                    // Barra de Búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar ticket o categoría") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            )
        },
        containerColor = Color.Transparent // Para el efecto edge-to-edge
    ) { innerPadding ->
        TicketsContent(
            innerPadding = innerPadding,
            // searchQuery = searchQuery, // No es necesario pasarlo
            selectedStatus = selectedStatus,
            selectedPriority = selectedPriority,
            selectedCategory = selectedCategory,
            onStatusChange = { selectedStatus = it },
            onPriorityChange = { selectedPriority = it },
            onCategoryChange = { selectedCategory = it },
            tickets = filteredTickets,
            onTicketClick = { ticketId -> navController.navigate("/ticket/detail/$ticketId") },
            showFilters = showFilters, // Pasar el estado de visibilidad
            activeFiltersCount = activeFiltersCount, // Pasar contador
            onClearFilters = clearAllFilters // Pasar función de limpiar
        )
    }
}

// --- Contenido de la Pantalla (Scrollable) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsContent(
    innerPadding: PaddingValues,
    // searchQuery: String,
    selectedStatus: TicketStatus?,
    selectedPriority: TicketPriority?,
    selectedCategory: TicketCategory?,
    onStatusChange: (TicketStatus?) -> Unit,
    onPriorityChange: (TicketPriority?) -> Unit,
    onCategoryChange: (TicketCategory?) -> Unit,
    tickets: List<Ticket>,
    onTicketClick: (String) -> Unit,
    showFilters: Boolean, // Recibe el estado
    activeFiltersCount: Int, // Recibe contador
    onClearFilters: () -> Unit // Recibe función
) {
    val topPadding = innerPadding.calculateTopPadding()
    val bottomPadding = innerPadding.calculateBottomPadding()
    val customColors = LocalCustomColors.current

    Column( // Usamos Column para poder poner el panel de filtros arriba
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
    ){
        // --- CAMBIO: Panel de Filtros Desplegable ---
        AnimatedVisibility(
            visible = showFilters,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(200)),
        ) {
            Surface( // Fondo blanco para el panel
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface, // bg-white
                shadowElevation = 2.dp, // Sombra sutil
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) // border-b
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), // px-6 py-4
                    verticalArrangement = Arrangement.spacedBy(16.dp) // space-y-4
                ) {
                    // Header del Panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = topPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtros",
                            style = MaterialTheme.typography.titleMedium, // text-base font-semibold
                            fontWeight = FontWeight.SemiBold
                        )
                        if (activeFiltersCount > 0) {
                            TextButton(onClick = onClearFilters) {
                                Text("Limpiar todo")
                            }
                        }
                    }

                    // Filtro de Estado
                    FilterSection(label = "Estado") {
                        items(statusFilterOptions) { status ->
                            val isSelected = selectedStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { onStatusChange(status) },
                                label = { Text(status?.displayName ?: "Todos") },
                                leadingIcon = { Icon(status?.icon ?: Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected),
                            )
                        }
                    }

                    // Filtro de Prioridad
                    FilterSection(label = "Prioridad") {
                        items(priorityFilterOptions) { priority ->
                            val isSelected = selectedPriority == priority
                            // --- CAMBIO: Icon color tinting ---
                            val iconColor = when (priority) {
                                TicketPriority.ALTA -> MaterialTheme.colorScheme.error
                                TicketPriority.MEDIA -> customColors.warning
                                TicketPriority.BAJA -> MaterialTheme.colorScheme.primary // O un color 'info'
                                null -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPriorityChange(priority) },
                                label = { Text(priority?.displayName ?: "Todas") },
                                leadingIcon = {
                                    Icon(
                                        priority?.icon ?: Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        tint = if(isSelected) LocalContentColor.current else iconColor // Tint icon when not selected
                                    )
                                },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected)
                            )
                        }
                    }

                    // Filtro de Categoría
                    FilterSection(label = "Categoría") {
                        items(categoryFilterOptions) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategoryChange(category) },
                                label = { Text(category?.displayName ?: "Todas") },
                                leadingIcon = { Icon(category?.icon ?: Icons.Default.Category, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected)
                            )
                        }
                    }
                }
            }
        }

        // --- CAMBIO: Fila de Información de Filtros/Resultados ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (!showFilters) topPadding + 16.dp else 16.dp) // Ajusta padding si filtros están ocultos
                .padding(horizontal = 16.dp, vertical = 8.dp), // px-6 py-3
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${tickets.size} ${if (tickets.size == 1) "ticket encontrado" else "tickets encontrados"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (activeFiltersCount > 0) {
                Text(
                    text = "$activeFiltersCount ${if (activeFiltersCount == 1) "filtro activo" else "filtros activos"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        // --- Lista de Tickets (en LazyColumn) ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f), // Ocupa el espacio restante
            // Quitamos padding superior, ya se maneja fuera
            contentPadding = PaddingValues(bottom = bottomPadding + 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre items
        ) {
            if (tickets.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth() // Ocupa ancho completo del LazyColumn
                            .padding(vertical = 64.dp, horizontal = 16.dp), // Padding
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No se encontraron tickets",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Intenta ajustar los filtros o la búsqueda",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // --- CAMBIO: Botón Limpiar Filtros en Empty State ---
                        if (activeFiltersCount > 0) {
                            Button(onClick = onClearFilters) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }
            } else {
                items(tickets, key = { it.id }) { ticket ->
                    TicketListItem(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        customColors = customColors // Pasamos los colores
                    )
                }
            }
        }
    }
}

// --- CAMBIO: Helper Composable para secciones de filtro ---
@Composable
fun FilterSection(
    label: String,
    content: LazyListScope.() -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall, // text-xs font-medium
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp) // mb-2
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // gap-2
            contentPadding = PaddingValues(bottom = 4.dp), // pb-2 for scrollbar room
            content = content
        )
    }
}

// --- CAMBIO: Helpers para estilo de FilterChip ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getFilterChipColors(isSelected: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
    containerColor = MaterialTheme.colorScheme.surfaceVariant, // bg-[var(--color-surface-variant)]
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getFilterChipBorder(isSelected: Boolean) = FilterChipDefaults.filterChipBorder(
    borderColor = Color.Transparent, // No border by default
    borderWidth = 0.dp,
    selectedBorderWidth = 0.dp,
    disabledBorderColor = Color.Transparent,
    selectedBorderColor = Color.Transparent,
    enabled = true,
    selected = isSelected
)


// --- Componente para un Item de la Lista de Tickets (Sin cambios) ---
@Composable
fun TicketListItem(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColors: CustomColors
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = ticket.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                PriorityBadge(priority = ticket.priority, customColors = customColors)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Label,
                    contentDescription = "Categoría",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ticket.category.displayName, // Usar displayName
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusChip(status = ticket.status, customColors = customColors)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ticket.assignedTo.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = ticket.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Componentes Helper StatusChip y PriorityBadge (Sin cambios) ---
@Composable
fun StatusChip(status: TicketStatus, customColors: CustomColors) {
    val (bgColor, contentColor, icon) = when (status) {
        TicketStatus.ABIERTO -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            null
        )
        TicketStatus.EN_PROGRESO -> Triple(
            customColors.warningContainer,
            customColors.warning,
            Icons.Default.Schedule
        )
        TicketStatus.CERRADO -> Triple(
            customColors.successContainer,
            customColors.success,
            Icons.Default.CheckCircleOutline
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = status.displayName,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PriorityBadge(priority: TicketPriority, customColors: CustomColors) {
    val (bgColor, contentColor, icon) = when (priority) {
        TicketPriority.ALTA -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            Icons.Default.ErrorOutline
        )
        TicketPriority.MEDIA -> Triple(
            customColors.warningContainer,
            customColors.warning,
            Icons.Default.WarningAmber
        )
        TicketPriority.BAJA -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondary,
            Icons.Default.KeyboardArrowDown
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = priority.displayName,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// --- Preview (Sin cambios) ---
@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            TicketsScreen(navController = navController)
        }
    }
}

