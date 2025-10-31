package dev.boni.techhelpdesk.ui.screens.knowledge.id

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
import dev.boni.techhelpdesk.ui.theme.CustomColors
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

sealed interface ArticleContentBlock {
    data class Paragraph(val text: String) : ArticleContentBlock
    data class Heading(val text: String) : ArticleContentBlock
    data class Steps(val items: List<String>) : ArticleContentBlock
    data class Warning(val text: String) : ArticleContentBlock
}

data class RelatedArticle(val id: String, val title: String, val icon: ImageVector)

data class ArticleData(
    val id: String,
    val title: String,
    val category: String,
    val views: Int,
    val helpful: Int,
    val lastUpdated: String,
    val content: List<ArticleContentBlock>,
    val relatedArticles: List<RelatedArticle>
)

val sampleArticle = ArticleData(
    id = "1",
    title = "Cómo restablecer tu contraseña de Windows",
    category = "Software",
    views = 1250,
    helpful = 98,
    lastUpdated = "15 de enero, 2025",
    content = listOf(
        ArticleContentBlock.Paragraph("Si olvidaste tu contraseña de Windows, puedes restablecerla siguiendo estos pasos. Este proceso funciona para Windows 10 y Windows 11."),
        ArticleContentBlock.Heading("Método 1: Usar disco de restablecimiento"),
        ArticleContentBlock.Steps(listOf(
            "Inserta tu disco de restablecimiento de contraseña en tu computadora",
            "En la pantalla de inicio de sesión, haz clic en 'Restablecer contraseña'",
            "Sigue el asistente de restablecimiento de contraseña",
            "Crea una nueva contraseña segura",
            "Inicia sesión con tu nueva contraseña"
        )),
        ArticleContentBlock.Heading("Método 2: Usar cuenta Microsoft"),
        ArticleContentBlock.Paragraph("Si tu cuenta está vinculada a Microsoft, puedes restablecer tu contraseña en línea:"),
        ArticleContentBlock.Steps(listOf(
            "Ve a account.microsoft.com/password/reset",
            "Selecciona 'Olvidé mi contraseña'",
            "Ingresa tu correo electrónico",
            "Verifica tu identidad usando el código enviado",
            "Crea una nueva contraseña"
        )),
        ArticleContentBlock.Warning("Importante: Asegúrate de crear una contraseña segura que incluya mayúsculas, minúsculas, números y símbolos.")
    ),
    relatedArticles = listOf(
        RelatedArticle("2", "Activar autenticación de dos factores", Icons.Default.VerifiedUser),
        RelatedArticle("3", "Configurar inicio de sesión biométrico", Icons.Default.Fingerprint)
    )
)

// --- Pantalla de Detalle del Artículo ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeArticleScreen(
    navController: NavController,
    articleId: String,
    modifier: Modifier = Modifier
) {
    val article = sampleArticle
    val customColors = LocalCustomColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- Tarjeta de Metadatos del Artículo ---
            item {
                ArticleMetaCard(
                    category = article.category,
                    views = article.views,
                    lastUpdated = article.lastUpdated
                )
            }

            // --- Tarjeta de Contenido del Artículo ---
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        article.content.forEachIndexed { index, block ->
                            when (block) {
                                is ArticleContentBlock.Paragraph -> ParagraphBlock(block.text)
                                is ArticleContentBlock.Heading -> HeadingBlock(block.text)
                                is ArticleContentBlock.Steps -> StepsBlock(block.items)
                                is ArticleContentBlock.Warning -> WarningBlock(block.text)
                            }
                        }
                    }
                }
            }

            // --- Sección de Feedback ---
            item {
                FeedbackCard(helpfulPercent = article.helpful) // <-- Modificado
            }

            // --- Artículos Relacionados ---
            item {
                Column {
                    Text(
                        "Artículos relacionados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        article.relatedArticles.forEach { related ->
                            RelatedArticleItem(
                                title = related.title,
                                icon = related.icon,
                                onClick = { /* navController.navigate("/knowledge/${related.id}") */ }
                            )
                        }
                    }
                }
            }

            // --- Contactar Soporte ---
            item {
                ContactSupportCard(
                    onClick = { /* navController.navigate("/support-chat") */ },
                    customColors = customColors
                )
            }
        } // Fin LazyColumn
    } // Fin Scaffold
}


// --- Componentes Helper para el Artículo ---

@Composable
fun ArticleMetaCard(category: String, views: Int, lastUpdated: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                MetaInfo(icon = Icons.Filled.Folder, text = category)
                MetaInfo(icon = Icons.Filled.Visibility, text = "$views vistas")
            }
            MetaInfo(icon = Icons.Filled.Schedule, text = "Actualizado: $lastUpdated")
        }
    }
}

@Composable
fun MetaInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ParagraphBlock(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
}

@Composable
fun HeadingBlock(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun StepsBlock(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(start = 8.dp)) {
        items.forEachIndexed { index, step ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(step, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), lineHeight = 22.sp)
            }
        }
    }
}

@Composable
fun WarningBlock(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Advertencia",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp).padding(end = 12.dp)
            )
            Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer, lineHeight = 22.sp)
        }
    }
}

@Composable
fun FeedbackCard(helpfulPercent: Int) {
    var selection by remember { mutableStateOf<Boolean?>(null) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = if (selection == null) "¿Te resultó útil?" else "¡Gracias por tu feedback!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // 3. El variant del botón "Sí" es dinámico
                val yesVariant = if (selection == true) MobileButtonVariant.FILLED else MobileButtonVariant.OUTLINED
                MobileButton(
                    onClick = { selection = true },
                    variant = yesVariant,
                    modifier = Modifier.weight(1f),
                    enabled = selection == null
                ) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sí (${helpfulPercent}%)")
                }

                // 6. El variant del botón "No" es dinámico
                val noVariant = if (selection == false) MobileButtonVariant.FILLED else MobileButtonVariant.OUTLINED
                MobileButton(
                    onClick = { selection = false },
                    variant = noVariant,
                    modifier = Modifier.weight(1f),
                    enabled = selection == null
                ) {
                    Icon(Icons.Filled.ThumbDown, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("No")
                }
            }
        }
    }
}

@Composable
fun RelatedArticleItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ContactSupportCard(onClick: () -> Unit, customColors: CustomColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.SupportAgent, contentDescription = null, modifier = Modifier.size(40.dp).padding(bottom = 12.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Text("¿Aún necesitas ayuda?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Text("Nuestro equipo de soporte está listo para asistirte", style = MaterialTheme.typography.bodyMedium, color = LocalContentColor.current.copy(alpha=0.8f), modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)
            MobileButton(onClick = onClick, variant = MobileButtonVariant.FILLED) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar chat")
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun KnowledgeArticleScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            KnowledgeArticleScreen(navController = navController, articleId = "1")
        }
    }
}