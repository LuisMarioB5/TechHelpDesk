// AppHeader.kt (Corregido con actions condicional)

package dev.boni.techhelpdesk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// Creamos una lambda vacía de referencia para comparar
private val EmptyActions: @Composable RowScope.() -> Unit = {}

/**
 * Cabecera de aplicación personalizada y flexible.
 * Mantiene el fondo primario y las esquinas inferiores redondeadas.
 *
 * @param modifier Modificador de Compose.
 * @param title El Composable para el título principal.
 * @param navigationIcon El Composable para el icono de navegación (ej. flecha atrás). (OPCIONAL)
 * @param actions El Composable para los iconos de acción (ej. perfil). Si es vacío, no ocupa espacio.
 * @param bottomContent El Composable para el contenido inferior (ej. subtítulo o barra de búsqueda).
 */
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    // --- CAMBIO: Volvemos a actions ---
    actions: @Composable RowScope.() -> Unit = EmptyActions, // Default a la referencia vacía
    // --- FIN CAMBIO ---
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    val headerShape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = headerShape
            )
            .statusBarsPadding()
            .padding(vertical = 20.dp)
    ) {
        // --- Fila Superior: Icono, Título, Acciones ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Slot para el Icono de Navegación (si existe)
            if (navigationIcon != null) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    navigationIcon()
                }
            } else {
                Spacer(Modifier.size(4.dp))
            }

            // Título
            Box(Modifier.padding(horizontal = if (navigationIcon != null) 12.dp else 4.dp)) {
                title()
            }

            // Spacer para empujar las acciones a la derecha
            Spacer(Modifier.weight(1f))

            // --- CAMBIO: Slot de Acciones Condicional ---
            // Solo dibujamos el contenedor si actions NO es la lambda vacía
            if (actions !== EmptyActions) {
                Row(
                    // Damos un tamaño mínimo si hay acciones, pero puede crecer
                    modifier = Modifier.height(48.dp), // Altura mínima para alinear
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions // Dibuja lo que se pase en 'actions'
                )
            }
            // Si 'actions' es EmptyActions, no se dibuja nada aquí,
            // y el Spacer(weight(1f)) ocupa todo el espacio hasta el borde.
            // --- FIN CAMBIO ---
        }

        // --- Slot Inferior: Subtítulo, Barra de Búsqueda, etc. ---
        if (bottomContent != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                bottomContent()
            }
        }
    }
}


// --- VISTAS PREVIAS (Actualizadas para usar 'actions') ---

@Preview(name = "Header - Dashboard", showBackground = true)
@Composable
fun DashboardHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            title = {
                Column {
                    Text("Hola, Luis", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text("Bienvenido de vuelta", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
            },
            // --- CAMBIO: Volvemos a pasar el IconButton a 'actions' ---
            actions = {
                IconButton(
                    onClick = { /* Ir al perfil */ },
                    modifier = Modifier
                        .size(48.dp) // El tamaño ahora va en el IconButton
                        .background(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), shape = CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(28.dp))
                }
            }
            // --- FIN CAMBIO ---
        )
    }
}

@Preview(name = "Header - Con Búsqueda", showBackground = true)
@Composable
fun SearchHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            title = {
                Text("Mis Tickets", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            },
            // NO pasamos 'actions' (usa el default EmptyActions), no ocupará espacio
            bottomContent = {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Buscar ticket o categoría", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        )
    }
}

@Preview(name = "Header - Detalle (Atrás)", showBackground = true)
@Composable
fun DetailHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            navigationIcon = {
                IconButton(onClick = { /* Navegar hacia atrás */ }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(28.dp))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ticket #T-2025-001", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)) {
                        Text("Abierto", modifier = Modifier.padding(horizontal=8.dp, vertical=4.dp), style=MaterialTheme.typography.labelSmall)
                    }
                }
            }
            // NO pasamos 'actions' ni 'bottomContent'
        )
    }
}