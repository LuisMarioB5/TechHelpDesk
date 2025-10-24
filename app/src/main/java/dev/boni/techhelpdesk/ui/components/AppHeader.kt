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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

/**
 * Cabecera de aplicación personalizada y flexible.
 * Mantiene el fondo primario y las esquinas inferiores redondeadas.
 *
 * @param modifier Modificador de Compose.
 * @param title El Composable para el título principal.
 * @param navigationIcon El Composable para el icono de navegación (ej. flecha atrás). (OPCIONAL)
 * @param actions El Composable para los iconos de acción (ej. perfil).
 * @param bottomContent El Composable para el contenido inferior (ej. subtítulo o barra de búsqueda).
 */
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null // Contenido opcional
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .statusBarsPadding() // Añade padding para la barra de estado
            .padding(horizontal = 16.dp, vertical = 20.dp) // Padding interno
    ) {
        // --- Fila Superior: Icono, Título, Acciones ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // El Slot solo se dibuja si navigationIcon no es nulo
            if (navigationIcon != null) {
                // Slot para el Icono de Navegación
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    navigationIcon()
                }
            } else {
                // Dejamos un pequeño espacio si no hay icono, para alinear con el padding general
                Spacer(modifier = Modifier.size(4.dp))
            }

            // Slot para el Título (ocupa el espacio restante)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (navigationIcon != null) 12.dp else 4.dp)
            ) {
                title()
            }

            // Slot para las Acciones
            Row(
                modifier = Modifier.size(48.dp), // Damos un tamaño fijo al contenedor de acciones
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }

        // --- Slot Inferior: Subtítulo, Barra de Búsqueda, etc. ---
        if (bottomContent != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Añadimos padding para alinear con el inicio del texto del título
                    .padding(horizontal = 4.dp)
            ) {
                bottomContent()
            }
        }
    }
}

// --- VISTAS PREVIAS ---

/**
 * PREVIEW 1: Cabecera del Dashboard (Hola, Luis)
 * Coincide con tu React code y la imagen 'image_4acdf2.png'
 * --- ARREGLADO ---
 */
@Preview(name = "Header - Dashboard", showBackground = true)
@Composable
fun DashboardHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            // navigationIcon se omite (es nulo)
            title = {
                // --- CAMBIO ---
                // Agrupamos los dos textos en una Columna
                // El Row principal de AppHeader (con Alignment.CenterVertically)
                // centrará esta Columna con el Icono de acciones.
                Column {
                    Text(
                        text = "Hola, Luis",
                        style = MaterialTheme.typography.headlineMedium, // text-28px
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Bienvenido de vuelta",
                        style = MaterialTheme.typography.bodyMedium, // text-14px
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { /* Ir al perfil */ },
                    modifier = Modifier
                        .size(48.dp) // Tamaño del botón
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            // --- CAMBIO ---
            // El contenido inferior ahora es nulo
            bottomContent = null
        )
    }
}

/**
 * PREVIEW 2: Cabecera de Tickets (con Barra de Búsqueda)
 * Coincide con la imagen 'image_4acdd3.png'
 */
@Preview(name = "Header - Con Búsqueda", showBackground = true)
@Composable
fun SearchHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            // navigationIcon se omite (es nulo)
            title = {
                Text(
                    text = "Mis Tickets",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            bottomContent = {
                // Esto simula un TextField. En tu app real, pondrías un OutlinedTextField
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Buscar ticket o categoría",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }
}

/**
 * PREVIEW 3: Cabecera de Detalle (con Flecha de Atrás)
 * Coincide con la imagen 'image_4acdb6.png'
 */
@Preview(name = "Header - Detalle (Atrás)", showBackground = true)
@Composable
fun DetailHeaderPreview() {
    TechHelpDeskTheme {
        AppHeader(
            // navigationIcon SÍ se pasa
            navigationIcon = {
                IconButton(
                    onClick = { /* Navegar hacia atrás */ },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Ticket #T-2025-001",
                    style = MaterialTheme.typography.titleLarge, // text-20px
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            // Sin 'actions' ni 'bottomContent'
        )
    }
}
