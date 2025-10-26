package dev.boni.techhelpdesk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.screens.DashboardScreen
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechHelpDeskTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation(navController = navController)
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "/dashboard",
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        composable(route = "/dashboard") {
                            // DashboardScreen maneja su propio padding superior
                            DashboardScreen(navController = navController)
                        }
                        composable(route = "/tickets") {
                            PlaceholderScreen(text = "Pantalla de Tickets")
                        }
                        composable(route = "/notifications") {
                            PlaceholderScreen(text = "Pantalla de Notificaciones")
                        }
                        composable(route = "/knowledge") {
                            PlaceholderScreen(text = "Pantalla de Conocimiento")
                        }
                        composable(route = "/profile") {
                            PlaceholderScreen(text = "Pantalla de Perfil")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Un Composable temporal para rellenar las pantallas que aún no has creado.
 */
@Composable
fun PlaceholderScreen(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TechHelpDeskTheme {
        PlaceholderScreen("Preview de pantalla")
    }
}
