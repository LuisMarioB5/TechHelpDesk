// NewConversationScreen.kt

package dev.boni.techhelpdesk.ui.screens // O el paquete correcto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.* // Importar todos
import androidx.compose.material.icons.outlined.* // Importar todos outlined
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.AppHeader // Reutilizamos AppHeader
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- Definiciones de Datos ---
data class Technician(
    val id: String,
    val name: String,
    val department: String,
    val isOnline: Boolean,
    val avatar: String
)

// --- Datos de Ejemplo ---
val sampleTechnicians = listOf(
    Technician("1", "Carlos Méndez", "Soporte Nivel 1", true, "C"),
    Technician("2", "Ana García", "Soporte Nivel 2", false, "A"),
    Technician("3", "Luis Torres", "Redes", true, "L"),
    Technician("4", "María Rodríguez", "Hardware", true, "M"),
    Technician("5", "Pedro Sánchez", "Software", false, "P"),
)

// --- Pantalla para Iniciar Nueva Conversación ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConversationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado ---
    var searchQuery by remember { mutableStateOf("") }

    val filteredTechnicians by remember(searchQuery, sampleTechnicians) {
        derivedStateOf {
            sampleTechnicians.filter { tech ->
                tech.name.contains(searchQuery, ignoreCase = true) ||
                        tech.department.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppHeader(
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
                        "Nuevo Chat",
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
                        placeholder = { Text("Buscar técnico o departamento...") },
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
        // --- SIN BottomNavigation ---
        containerColor = Color.Transparent // Para efecto edge-to-edge
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding), // Aplicar padding del Scaffold
            contentPadding = PaddingValues(bottom = 24.dp) // Padding al final
        ) {

            // --- Título de la Lista ---
            item {
                Text(
                    text = "TÉCNICOS DISPONIBLES",
                    style = MaterialTheme.typography.labelMedium, // text-[14px]
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp) // px-6 mb-3 px-2
                )
            }

            // --- Lista de Técnicos o Estado Vacío ---
            if (filteredTechnicians.isEmpty()) {
                item {
                    EmptyTechnicianState()
                }
            } else {
                items(filteredTechnicians, key = { it.id }) { technician ->
                    TechnicianListItem(
                        technician = technician,
                        onClick = {
                            // Navega a la pantalla de detalle, pasando el ID del técnico
                            navController.navigate("/conversation/detail/${technician.id}")
                        }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// --- Componente Helper: Item de Técnico ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianListItem(
    technician: Technician,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface // bg-[var(--color-surface)]
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), // p-4
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp) // gap-4
        ) {
            // --- Avatar con Badge ---
            BadgedBox(
                badge = {
                    Box(
                        modifier = Modifier
                            .size(14.dp) // w-3.5 h-3.5
                            .clip(CircleShape)
                            .background(if (technician.isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline) // bg-green-500 o bg-outline
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape) // border-2
                    )
                },
                modifier = Modifier.size(48.dp) // w-12 h-12
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        technician.avatar,
                        style = MaterialTheme.typography.titleSmall, // text-[18px]
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // --- Contenido (Nombre y Departamento) ---
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp) // mb-0.5
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = technician.name,
                        style = MaterialTheme.typography.bodyLarge, // text-[16px]
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (technician.isOnline) {
                        Text(
                            "En línea",
                            style = MaterialTheme.typography.labelSmall, // text-[12px]
                            color = Color(0xFF4CAF50), // text-green-600
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text = technician.department,
                    style = MaterialTheme.typography.bodyMedium, // text-[14px]
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // --- Chevron ---
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Componente Helper: Estado Vacío ---
@Composable
fun EmptyTechnicianState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 16.dp), // py-12 px-6
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp)) // mb-4
        Text(
            "No se encontraron técnicos",
            style = MaterialTheme.typography.bodyLarge, // text-[16px]
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun NewConversationScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            NewConversationScreen(navController = navController)
        }
    }
}