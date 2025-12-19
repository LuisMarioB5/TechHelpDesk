package dev.boni.techhelpdesk.ui.screens.tickets

import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.data.model.Ticket
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.screens.viewmodels.TicketUiState
import dev.boni.techhelpdesk.ui.screens.viewmodels.TicketViewModel
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    navController: NavController,
    initialFilterStatus: String? = null,
    viewModel: TicketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    val initialStatusEnum = TicketStatusUI.fromString(initialFilterStatus)
    var selectedStatus by remember { mutableStateOf(initialStatusEnum) }
    var selectedPriority by remember { mutableStateOf<TicketPriorityUI?>(null) }
    var selectedCategory by remember { mutableStateOf<TicketCategoryUI?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val isTechnician by viewModel.isTechnician.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkRoleAndLoadTickets()
    }

    val allTickets = when (val state = uiState) {
        is TicketUiState.Success -> state.tickets
        else -> emptyList()
    }

    Log.d("TicketsScreen", "All Tickets: $allTickets")

    val filteredTickets by remember(allTickets, searchQuery, selectedStatus, selectedPriority, selectedCategory) {
        derivedStateOf {
            allTickets.filter { ticket ->
                val ticketCategoryUI = TicketCategoryUI.fromString(ticket.category)
                val ticketStatusUI = TicketStatusUI.fromString(ticket.status)
                val ticketPriorityUI = TicketPriorityUI.fromString(ticket.priority)

                val categoryLabel = ticketCategoryUI?.let { context.getString(it.labelResId) } ?: ""

                val matchesSearch = searchQuery.isBlank() ||
                        ticket.title.contains(searchQuery, ignoreCase = true) ||
                        categoryLabel.contains(searchQuery, ignoreCase = true) ||
                        ticket.id.contains(searchQuery, ignoreCase = true)

                val matchesStatus = selectedStatus == null || ticketStatusUI == selectedStatus
                val matchesPriority = selectedPriority == null || ticketPriorityUI == selectedPriority
                val matchesCategory = selectedCategory == null || ticketCategoryUI == selectedCategory

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
                        text = if (isTechnician) stringResource(R.string.title_technician_panel) else stringResource(R.string.title_my_tickets),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
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
                                contentDescription = stringResource(R.string.cd_filters)
                            )
                        }
                    }
                },
                bottomContent = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.placeholder_search_tickets)) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("/ticket/create") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_add_ticket)
                )
            }
        },
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigation(navController = navController)
        },
    ) { innerPadding ->
        if (uiState is TicketUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            TicketsContent(
                innerPadding = innerPadding,
                selectedStatus = selectedStatus,
                selectedPriority = selectedPriority,
                selectedCategory = selectedCategory,
                onStatusChange = { selectedStatus = it },
                onPriorityChange = { selectedPriority = it },
                onCategoryChange = { selectedCategory = it },
                tickets = filteredTickets,
                onTicketClick = { ticketId -> navController.navigate("/ticket/detail/$ticketId") },
                showFilters = showFilters,
                activeFiltersCount = activeFiltersCount,
                onClearFilters = clearAllFilters
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsContent(
    innerPadding: PaddingValues,
    selectedStatus: TicketStatusUI?,
    selectedPriority: TicketPriorityUI?,
    selectedCategory: TicketCategoryUI?,
    onStatusChange: (TicketStatusUI?) -> Unit,
    onPriorityChange: (TicketPriorityUI?) -> Unit,
    onCategoryChange: (TicketCategoryUI?) -> Unit,
    tickets: List<Ticket>,
    onTicketClick: (String) -> Unit,
    showFilters: Boolean,
    activeFiltersCount: Int,
    onClearFilters: () -> Unit
) {
    val topPadding = innerPadding.calculateTopPadding()
    val bottomPadding = innerPadding.calculateBottomPadding()
    val customColors = LocalCustomColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
    ){
        AnimatedVisibility(
            visible = showFilters,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(200)),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = topPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.title_filters_panel),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (activeFiltersCount > 0) {
                            TextButton(onClick = onClearFilters) {
                                Text(stringResource(R.string.btn_clear_all))
                            }
                        }
                    }

                    FilterSection(label = stringResource(R.string.label_filter_status)) {
                        items(statusFilterOptions) { status ->
                            val isSelected = selectedStatus == status
                            val label = status?.let { stringResource(it.labelResId) } ?: stringResource(R.string.filter_all)

                            FilterChip(
                                selected = isSelected,
                                onClick = { onStatusChange(status) },
                                label = { Text(label) },
                                leadingIcon = { Icon(status?.icon ?: Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected),
                            )
                        }
                    }

                    FilterSection(label = stringResource(R.string.label_filter_priority)) {
                        items(priorityFilterOptions) { priority ->
                            val isSelected = selectedPriority == priority
                            val label = priority?.let { stringResource(it.labelResId) } ?: stringResource(R.string.filter_all)

                            val iconColor = when (priority) {
                                TicketPriorityUI.ALTA -> MaterialTheme.colorScheme.error
                                TicketPriorityUI.MEDIA -> customColors.warning
                                TicketPriorityUI.BAJA -> customColors.success
                                null -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPriorityChange(priority) },
                                label = { Text(label) },
                                leadingIcon = {
                                    Icon(
                                        priority?.icon ?: Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        tint = if(isSelected) LocalContentColor.current else iconColor
                                    )
                                },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected)
                            )
                        }
                    }

                    FilterSection(label = stringResource(R.string.label_filter_category)) {
                        items(categoryFilterOptions) { category ->
                            val isSelected = selectedCategory == category
                            val label = category?.let { stringResource(it.labelResId) } ?: stringResource(R.string.filter_all)

                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategoryChange(category) },
                                label = { Text(label) },
                                leadingIcon = { Icon(category?.icon ?: Icons.Default.Category, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
                                colors = getFilterChipColors(isSelected),
                                border = getFilterChipBorder(isSelected)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (!showFilters) topPadding + 16.dp else 16.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val countText = if (tickets.size == 1) stringResource(R.string.text_ticket_found) else stringResource(R.string.text_tickets_found)
            Text(
                text = "${tickets.size} $countText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (activeFiltersCount > 0) {
                val filterText = if (activeFiltersCount == 1) stringResource(R.string.text_filter_active) else stringResource(R.string.text_filters_active)
                Text(
                    text = "$activeFiltersCount $filterText",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = bottomPadding + 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (tickets.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 64.dp, horizontal = 16.dp),
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
                            text = stringResource(R.string.empty_tickets_title),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.empty_tickets_desc),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (activeFiltersCount > 0) {
                            Button(onClick = onClearFilters) {
                                Text(stringResource(R.string.btn_clear_filters))
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
                        customColors = customColors
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    label: String,
    content: LazyListScope.() -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 4.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getFilterChipColors(isSelected: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getFilterChipBorder(isSelected: Boolean) = FilterChipDefaults.filterChipBorder(
    borderColor = Color.Transparent,
    borderWidth = 0.dp,
    selectedBorderWidth = 0.dp,
    disabledBorderColor = Color.Transparent,
    selectedBorderColor = Color.Transparent,
    enabled = true,
    selected = isSelected
)

@Composable
fun TicketListItem(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColors: CustomColors
) {
    val categoryUI = TicketCategoryUI.fromString(ticket.category)

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
                    imageVector = categoryUI?.icon ?: Icons.Default.MoreHoriz,
                    contentDescription = "Categoría",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = categoryUI?.let { stringResource(it.labelResId) } ?: ticket.category,
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
                            text = ticket.assignedToName.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.text_recent),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String, customColors: CustomColors) {
    val statusUI = TicketStatusUI.fromString(status) ?: TicketStatusUI.ABIERTO

    val (bgColor, contentColor, icon) = when (statusUI) {
        TicketStatusUI.ABIERTO -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            null
        )
        TicketStatusUI.EN_PROGRESO -> Triple(
            customColors.warningContainer,
            customColors.warning,
            Icons.Default.Schedule
        )
        TicketStatusUI.RESUELTO, TicketStatusUI.CERRADO -> Triple(
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
            text = stringResource(statusUI.labelResId),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PriorityBadge(priority: String, customColors: CustomColors) {
    val priorityUI = TicketPriorityUI.fromString(priority) ?: TicketPriorityUI.MEDIA

    val (bgColor, contentColor, icon) = when (priorityUI) {
        TicketPriorityUI.ALTA -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            Icons.Default.ErrorOutline
        )
        TicketPriorityUI.MEDIA -> Triple(
            customColors.warningContainer,
            customColors.warning,
            Icons.Default.WarningAmber
        )
        TicketPriorityUI.BAJA -> Triple(
            customColors.successContainer,
            customColors.success,
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
            text = stringResource(priorityUI.labelResId),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// --- Preview  ---
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

