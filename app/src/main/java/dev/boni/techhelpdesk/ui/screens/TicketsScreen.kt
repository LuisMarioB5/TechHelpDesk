package dev.boni.techhelpdesk.ui.screens.ti

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline // Usado en StatusChip
import androidx.compose.material.icons.filled.ErrorOutline // Para Alta prioridad
import androidx.compose.material.icons.filled.Inbox // Para "No tickets"
import androidx.compose.material.icons.filled.KeyboardArrowDown // Para Baja prioridad
import androidx.compose.material.icons.filled.Label // Para Categoría
import androidx.compose.material.icons.filled.Schedule // Usado en StatusChip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber // Para Media prioridad (sustituto)
// import androidx.compose.material3.CircularProgressIndicator // No se usa
// import androidx.compose.material3.RadioButton // No se usa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors // Usado en Preview
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- Definiciones de Datos (Simplificadas) ---
enum class TicketStatus(val displayName: String) {
    ABIERTO("Abierto"),
    EN_PROGRESO("En progreso"),
    CERRADO("Cerrado")
}

enum class TicketPriority(val displayName: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

data class Ticket(
    val id: String,
    val title: String,
    val category: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    val assignedTo: String, // Simplificado a String
    val date: String
)

// --- Datos de Ejemplo ---
val sampleTickets = listOf(
    Ticket("T-2025-001", "Correo no envía adjuntos", "Email", TicketStatus.ABIERTO, TicketPriority.ALTA, "CM", "Hace 2h"),
    Ticket("T-2025-002", "Impresora no responde", "Hardware", TicketStatus.EN_PROGRESO, TicketPriority.MEDIA, "AG", "Hace 5h"),
    Ticket("T-2025-003", "Acceso a carpeta", "Permisos", TicketStatus.ABIERTO, TicketPriority.BAJA, "LT", "Hace 1d"),
    Ticket("T-2025-004", "Actualización software", "Software", TicketStatus.CERRADO, TicketPriority.MEDIA, "ML", "Hace 2d"),
    Ticket("T-2025-005", "VPN no conecta", "Red", TicketStatus.EN_PROGRESO, TicketPriority.ALTA, "CM", "Hace 3h"),
)

// --- Pantalla Principal de Tickets ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<TicketStatus?>(null) } // null significa "Todos"

    val filteredTickets by remember {
        derivedStateOf {
            sampleTickets.filter { ticket ->
                val matchesSearch = searchQuery.isBlank() ||
                        ticket.title.contains(searchQuery, ignoreCase = true) ||
                        ticket.category.contains(searchQuery, ignoreCase = true)
                val matchesFilter = selectedFilter == null || ticket.status == selectedFilter
                matchesSearch && matchesFilter
            }
        }
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
                            // Cursor color etc. can be added if needed
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.Transparent // Para el efecto edge-to-edge
    ) { innerPadding ->
        TicketsContent(
            innerPadding = innerPadding,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it },
            tickets = filteredTickets,
            onTicketClick = { ticketId -> /* navController.navigate("/tickets/$ticketId") */ }
        )
    }
}

// --- Contenido de la Pantalla (Scrollable) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsContent(
    innerPadding: PaddingValues,
    searchQuery: String, // Pasado solo para ejemplo, no se usa aquí directamente
    selectedFilter: TicketStatus?,
    onFilterChange: (TicketStatus?) -> Unit,
    tickets: List<Ticket>,
    onTicketClick: (String) -> Unit
) {
    val topPadding = innerPadding.calculateTopPadding()
    val bottomPadding = innerPadding.calculateBottomPadding()
    val customColors = LocalCustomColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Fondo de la lista
        contentPadding = PaddingValues(
            top = topPadding + 16.dp, // Espacio después del header
            bottom = bottomPadding + 16.dp // Espacio antes de la barra inferior
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre items
    ) {
        // --- Filtros ---
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = listOf(null) + TicketStatus.entries // null es "Todos"
                items(filterOptions) { status ->
                    val isSelected = selectedFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(status) },
                        label = { Text(status?.displayName ?: "Todos") },
                        // --- ¡ARREGLO AQUÍ! ---
                        colors = FilterChipDefaults.filterChipColors(
                            // Colores cuando está seleccionado
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            // Colores cuando NO está seleccionado
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            // Definimos el color del borde para el estado NO seleccionado
                            disabledContainerColor = MaterialTheme.colorScheme.surface, // Necesario para que el borde se vea
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        // El borde se controla aquí para el estado NO seleccionado
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outline,
                            borderWidth = if (!isSelected) 1.dp else 0.dp, // Mostrar borde solo si no está seleccionado
                            // Pasamos los valores requeridos, aunque no los usemos directamente aquí
                            selectedBorderWidth = 0.dp,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha=0.5f), // Ejemplo
                            selectedBorderColor = Color.Transparent, // No borde cuando seleccionado
                            enabled = true, // Asumimos que siempre está habilitado
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // --- Lista de Tickets ---
        if (tickets.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

// --- Componente para un Item de la Lista de Tickets (en el mismo archivo) ---
@Composable
fun TicketListItem(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColors: CustomColors // Recibimos los colores
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // rounded-2xl
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp // shadow-sm
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // p-4
            // Header: ID, Title, Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = ticket.id,
                        style = MaterialTheme.typography.labelSmall, // text-xs
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp) // mb-1
                    )
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.bodyLarge, // text-base
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp) // mb-2
                    )
                }
                PriorityBadge(priority = ticket.priority, customColors = customColors)
            }

            // Category
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp), // gap-2
                modifier = Modifier.padding(bottom = 12.dp) // mb-3
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Categoría",
                    modifier = Modifier.size(16.dp), // text-sm
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ticket.category,
                    style = MaterialTheme.typography.bodySmall, // text-sm
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Footer: Status, Assignee, Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusChip(status = ticket.status, customColors = customColors)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // gap-2
                ) {
                    // Assignee Avatar (simple initial)
                    Box(
                        modifier = Modifier
                            .size(24.dp) // w-6 h-6
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ticket.assignedTo.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 10.sp, // text-xs
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = ticket.date,
                        style = MaterialTheme.typography.labelSmall, // text-xs
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Componentes Helper (en el mismo archivo) ---

@Composable
fun StatusChip(status: TicketStatus, customColors: CustomColors) {
    val (bgColor, contentColor, icon) = when (status) {
        TicketStatus.ABIERTO -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            null // Podrías poner un icono si quieres
        )
        TicketStatus.EN_PROGRESO -> Triple(
            customColors.warningContainer,
            customColors.warning,
            Icons.Default.Schedule // Usamos Schedule como sustituto
        )
        TicketStatus.CERRADO -> Triple(
            customColors.successContainer,
            customColors.success,
            Icons.Default.CheckCircleOutline // Usamos CheckCircleOutline como sustituto
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
            style = MaterialTheme.typography.labelSmall, // text-xs
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
            Icons.Default.WarningAmber // Usamos WarningAmber como sustituto
        )
        TicketPriority.BAJA -> Triple(
            // Podrías crear un color "info" o usar un gris
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondary,
            Icons.Default.KeyboardArrowDown // Usamos KeyboardArrowDown como sustituto
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
            style = MaterialTheme.typography.labelSmall, // text-xs
            fontWeight = FontWeight.Medium
        )
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    TechHelpDeskTheme {
        // Proveemos los colores custom para la preview
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            TicketsScreen(navController = navController)
        }
    }
}

