package dev.boni.techhelpdesk.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

data class NavItem(
    val label: String,
    val route: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

/**
 * Barra de navegación inferior. Requiere un NavController para funcionar.
 *
 * @param navController El controlador de navegación de Compose.
 * @param modifier Modificador de Compose.
 */
@Composable
fun BottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("Inicio", "/dashboard", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Tickets", "/tickets?status={status}", Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber),
        NavItem("Chat", "/conversation", Icons.AutoMirrored.Filled.Chat,
            Icons.AutoMirrored.Outlined.Chat
        ),
        NavItem("Perfil", "/profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Color para el borde superior
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    NavigationBar(
        modifier = modifier
            .shadow(4.dp) // Sombra
            .drawBehind {
                // Dibuja la línea superior
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            val icon = if (isSelected) item.iconFilled else item.iconOutlined

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Obtenemos la ruta base actual (sin argumentos como ?status=...)
                    val currentBaseRoute = navBackStackEntry?.destination?.route?.substringBefore('?')
                    // Obtenemos la ruta de inicio (la pantalla principal del grafo)
                    val startDestinationRoute = navController.graph.findStartDestination().route

                    if (item.route == startDestinationRoute) { // Si el item clicado ES la pantalla de inicio (ej: "/dashboard")
                        if (currentBaseRoute != startDestinationRoute) {
                            // Y SI NO estamos ya en la pantalla de inicio,
                            // simplemente volvemos atrás.
                            navController.popBackStack()
                        }
                        // Si ya estamos en la pantalla de inicio, no hacemos nada (evita recargas innecesarias)

                    } else { // Si el item clicado NO ES la pantalla de inicio (ej: "/tickets", "/knowledge")
                        // Usamos la lógica estándar para cambiar a OTRA pestaña principal.
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }, // Fin del onClick


                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 1.sp // leading-none
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    // Colores cuando está activo (isActive)
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    // Colores cuando está inactivo
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

// --- VISTA PREVIA AÑADIDA ---
@Preview(showBackground = true, name = "Bottom Navigation Preview")
@Composable
fun BottomNavigationPreview() {
    TechHelpDeskTheme {
        // 1. Creamos un NavController de prueba
        val navController = rememberNavController()

        // 2. Usamos un Scaffold para darle un lugar a la barra inferior
        Scaffold(
            bottomBar = {
                // 3. Llamamos a tu componente con el NavController de prueba
                BottomNavigation(navController = navController)
            }
        ) { innerPadding ->
            // 4. Creamos un NavHost FALSO que define las rutas
            //    Esto es necesario para que 'currentBackStackEntryAsState' funcione
            NavHost(
                navController = navController,
                startDestination = "/dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("/dashboard") { Text("Página de Inicio") }
                composable("/tickets") { Text("Página de Tickets") }
                composable("/conversation") { Text("Página de Conversaciones (soporte)") }
                composable("/profile") { Text("Página de Perfil") }
            }
        }
    }
}
