package dev.boni.techhelpdesk.ui.screens.knowledge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

data class ArticleCategory(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val colorKey: String // "primary", "secondary", etc.
)

data class Article(
    val id: String,
    val title: String,
    val category: String, // Coincide con ArticleCategory.id
    val views: Int,
    val helpful: Int, // Porcentaje
    val icon: ImageVector,
    val colorKey: String // "primary", "secondary", etc.
)

// --- Datos de Ejemplo ---
val knowledgeCategories = listOf(
    ArticleCategory("all", "Todos", Icons.Default.Apps, "primary"),
    ArticleCategory("hardware", "Hardware", Icons.Default.Computer, "secondary"),
    ArticleCategory("software", "Software", Icons.Default.Code, "tertiary"), // Asumiendo tertiary
    ArticleCategory("network", "Red", Icons.Default.Wifi, "success"),
    ArticleCategory("security", "Seguridad", Icons.Default.Shield, "error"),
)

val knowledgeArticles = listOf(
    Article("1", "Cómo restablecer tu contraseña de Windows", "software", 1250, 98, Icons.Default.LockReset, "tertiary"),
    Article("2", "Solucionar problemas de conexión a WiFi", "network", 890, 95, Icons.Default.WifiOff, "success"),
    Article("3", "Configurar impresora de red", "hardware", 756, 92, Icons.Default.Print, "secondary"),
    Article("4", "Activar autenticación de dos factores", "security", 645, 97, Icons.Default.VerifiedUser, "error"),
    Article("5", "Instalar software corporativo", "software", 534, 89, Icons.Default.Download, "tertiary"),
    Article("6", "Reportar equipo dañado", "hardware", 423, 94, Icons.Default.ReportProblem, "secondary"),
)


// --- Pantalla Principal de Base de Conocimiento ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    val customColors = LocalCustomColors.current

    // --- Lógica de Filtro ---
    val filteredArticles by remember(searchQuery, selectedCategory) {
        derivedStateOf {
            knowledgeArticles.filter { article ->
                val matchesCategory = selectedCategory == "all" || article.category == selectedCategory
                val matchesSearch = article.title.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }
    }
    val popularArticles by remember {
        derivedStateOf {
            knowledgeArticles.sortedByDescending { it.views }.take(3)
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                // Sin icono de navegación para la pantalla principal de la pestaña
                title = {
                    Text(
                        "Base de conocimiento",
                        style = MaterialTheme.typography.headlineMedium, // text-[28px]
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
                        placeholder = { Text("Buscar artículos...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp), // rounded-[16px]
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium, // text-[14px]
                        singleLine = true
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.Transparent // Para efecto edge-to-edge
    ) { innerPadding ->

        // --- Contenido de la Pantalla ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 24.dp, // Espacio después del header
                bottom = innerPadding.calculateBottomPadding() + 24.dp // Espacio antes de nav y al final
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp) // space-y-6
        ) {
            // --- Sección de Categorías ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) { // px-6
                    Text(
                        "Categorías",
                        style = MaterialTheme.typography.titleMedium, // text-[16px] font-semibold
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp) // mb-3
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // gap-2
                        contentPadding = PaddingValues(bottom = 4.dp) // pb-2
                    ) {
                        items(knowledgeCategories) { category ->
                            val isSelected = selectedCategory == category.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category.id },
                                label = { Text(category.label, style = MaterialTheme.typography.labelMedium) }, // text-[14px]
                                leadingIcon = { Icon(category.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }, // text-[18px]
                                shape = RoundedCornerShape(16.dp), // rounded-[16px]
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // --- Sección Más Populares (Condicional) ---
            if (selectedCategory == "all" && searchQuery.isBlank()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Más populares",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
                items(popularArticles, key = { "popular-${it.id}" }) { article ->
                    PopularArticleCard(
                        article = article,
                        index = popularArticles.indexOf(article) + 1,
                        onClick = { navController.navigate("/knowledge/article/${article.id}") },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // --- Sección Todos los Artículos / Resultados ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        if (searchQuery.isNotBlank()) "Resultados (${filteredArticles.size})" else "Todos los artículos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            if (filteredArticles.isEmpty()) {
                item {
                    NoResultsMessage(modifier = Modifier.padding(horizontal = 16.dp))
                }
            } else {
                items(filteredArticles, key = { it.id }) { article ->
                    ArticleListItem(
                        article = article,
                        onClick = { navController.navigate("/knowledge/article/${article.id}") },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        customColors = customColors
                    )
                }
            }
        } // Fin LazyColumn
    } // Fin Scaffold
}

// --- Componente Helper: Tarjeta Artículo Popular ---
@Composable
fun PopularArticleCard(
    article: Article,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp), // rounded-[20px]
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient( // bg-gradient-to-r
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer // De primary a secondary container
                        )
                    )
                )
                .padding(16.dp), // p-4
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp) // gap-4
        ) {
            Box( // Contenedor del número
                modifier = Modifier
                    .size(48.dp) // w-12 h-12
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp), // Espacio extra
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$index",
                    style = MaterialTheme.typography.titleMedium, // text-[20px]
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f)) { // flex-1
                Text(
                    article.title,
                    style = MaterialTheme.typography.labelLarge, // text-[14px]
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // line-clamp-1
                    modifier = Modifier.padding(bottom = 4.dp) // mb-1
                )
                ArticleStats(views = article.views, helpful = article.helpful)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Componente Helper: Item de Artículo (General) ---
@Composable
fun ArticleListItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColors: CustomColors
) {
    // Mapear el colorKey a los colores del tema
    val (bgColor, contentColor) = when (article.colorKey) {
        "primary" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        "secondary" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.secondary
        "tertiary" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.tertiary
        "success" -> customColors.successContainer to customColors.success
        "error" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top, // items-start
            horizontalArrangement = Arrangement.spacedBy(16.dp) // gap-4
        ) {
            Box( // Contenedor del icono
                modifier = Modifier
                    .size(48.dp) // w-12 h-12
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = article.icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp) // text-[24px]
                )
            }
            Column(modifier = Modifier.weight(1f)) { // flex-1
                Text(
                    article.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis, // line-clamp-2
                    modifier = Modifier.padding(bottom = 8.dp) // mb-2
                )
                ArticleStats(views = article.views, helpful = article.helpful)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Componente Helper: Estadísticas (Vistas y Útil) ---
@Composable
fun ArticleStats(views: Int, helpful: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp), // gap-3
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$views", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$helpful%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Componente Helper: Sin Resultados ---
@Composable
fun NoResultsMessage(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(32.dp), // p-8
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp) // mb-3
        ) {
            Icon(Icons.Filled.SearchOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("No se encontraron artículos", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun KnowledgeBaseScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            KnowledgeBaseScreen(navController = navController)
        }
    }
}