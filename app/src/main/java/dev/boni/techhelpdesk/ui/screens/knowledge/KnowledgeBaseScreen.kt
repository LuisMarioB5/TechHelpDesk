// KnowledgeBaseScreen.kt (Modificado)

package dev.boni.techhelpdesk.ui.screens.knowledge // O el paquete correcto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // <-- Importado
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader // Reutilizamos AppHeader
// import dev.boni.techhelpdesk.ui.components.BottomNavigation // Ya no se necesita
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

data class ArticleCategory(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val colorKey: String
)

data class Article(
    val id: String,
    val title: String,
    val category: String,
    val views: Int,
    val helpful: Int,
    val icon: ImageVector,
    val colorKey: String
)

val knowledgeCategories = listOf(
    ArticleCategory("all", "Todos", Icons.Default.Apps, "primary"),
    ArticleCategory("hardware", "Hardware", Icons.Default.Computer, "secondary"),
    ArticleCategory("software", "Software", Icons.Default.Code, "tertiary"),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    val customColors = LocalCustomColors.current

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
                // --- ¡CAMBIO 1: Añadido botón de atrás! ---
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        "Base de conocimiento",
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
                        placeholder = { Text("Buscar artículos...") },
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
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true
                    )
                }
            )
        },
        // --- ¡CAMBIO 2: BottomNavigation eliminada! ---
        // bottomBar = {
        //     BottomNavigation(navController = navController)
        // },
        containerColor = Color.Transparent
    ) { innerPadding ->

        // --- Contenido de la Pantalla ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 24.dp,
                // El padding inferior ahora solo necesita el del sistema (que viene de innerPadding) + 24dp
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // (El resto del contenido de LazyColumn: Categorías, Populares, Artículos... no cambia)

            // --- Sección de Categorías ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "Categorías",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(knowledgeCategories) { category ->
                            val isSelected = selectedCategory == category.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category.id },
                                label = { Text(category.label, style = MaterialTheme.typography.labelMedium) },
                                leadingIcon = { Icon(category.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                shape = RoundedCornerShape(16.dp),
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
                            color = MaterialTheme.colorScheme.onBackground,
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
                        color = MaterialTheme.colorScheme.onBackground,
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

// --- (Todos los componentes Helper: PopularArticleCard, ArticleListItem, ArticleStats, NoResultsMessage
//       y la Preview siguen aquí debajo, sin cambios) ---

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
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    article.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ArticleStats(views = article.views, helpful = article.helpful)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = article.icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    article.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ArticleStats(views = article.views, helpful = article.helpful)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Componente Helper: Estadísticas (Vistas y Útil) ---
@Composable
fun ArticleStats(views: Int, helpful: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
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