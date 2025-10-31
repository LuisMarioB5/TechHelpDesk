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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.boni.techhelpdesk.ui.screens.CreateTicketScreen
import dev.boni.techhelpdesk.ui.screens.DashboardScreen
import dev.boni.techhelpdesk.ui.screens.LoginScreen
import dev.boni.techhelpdesk.ui.screens.RegisterScreen
import dev.boni.techhelpdesk.ui.screens.ForgotPasswordScreen
import dev.boni.techhelpdesk.ui.screens.NotificationsScreen
import dev.boni.techhelpdesk.ui.screens.ProfileScreen
import dev.boni.techhelpdesk.ui.screens.SplashScreen
import dev.boni.techhelpdesk.ui.screens.knowledge.KnowledgeBaseScreen
import dev.boni.techhelpdesk.ui.screens.knowledge.id.KnowledgeArticleScreen
import dev.boni.techhelpdesk.ui.screens.tickets.TicketsScreen
import dev.boni.techhelpdesk.ui.screens.tickets.id.TicketDetailScreen
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeSetting by remember { mutableStateOf("system") }

            TechHelpDeskTheme (themeSetting = themeSetting) {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "/splash",
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        composable(route = "/splash") {
                            SplashScreen(
                                onNavigateToLogin = { navController.navigate("/login") },
                                onNavigateToRegister = { navController.navigate("/register") }
                            )
                        }

                        composable(route = "/dashboard") {
                            // DashboardScreen maneja su propio padding superior
                            DashboardScreen(navController = navController)
                        }

                        composable(route = "/login") {
                            LoginScreen(navController = navController)
                        }

                        composable(route = "/forgot-password") {
                            ForgotPasswordScreen(navController = navController)
                        }

                        composable(route = "/register") {
                            RegisterScreen(navController = navController)
                        }

                        composable(
                            route = "/tickets?status={status}",
                            // 2. Definimos el argumento 'status'
                            arguments = listOf(
                                navArgument("status") {
                                    type = NavType.StringType // Es un texto
                                    nullable = true        // Puede ser nulo (si no se pasa filtro)
                                    defaultValue = null      // Valor por defecto si no se pasa
                                }
                            )
                        ) { backStackEntry -> // 'backStackEntry' contiene los argumentos
                            // 3. Leemos el argumento 'status' que llegó
                            val initialStatus = backStackEntry.arguments?.getString("status")

                            // 4. Llamamos a TicketsScreen pasándole el filtro inicial
                            TicketsScreen(
                                navController = navController,
                                initialFilterStatus = initialStatus
                            )
                        }
                        composable(
                            // La ruta base es diferente para evitar conflictos con /tickets?status
                            route = "/ticket/detail/{ticketId}",
                            arguments = listOf(
                                navArgument("ticketId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: "ID_INVALIDO"

                            TicketDetailScreen(
                                navController = navController,
                                ticketId = ticketId
                            )
                        }
                        composable(route = "/ticket/create") {
                            CreateTicketScreen(
                                navController = navController,
                            )
                        }

                        composable(route = "/knowledge") {
                            KnowledgeBaseScreen(navController = navController)
                        }
                        composable(
                            route = "/knowledge/article/{articleId}",
                            arguments = listOf(
                                navArgument("articleId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val articleId = backStackEntry.arguments?.getString("articleId") ?: "ID_INVALIDO"

                            KnowledgeArticleScreen(
                                navController = navController,
                                articleId = articleId,
                            )
                        }

                        composable(route = "/profile") {
                            ProfileScreen(
                                navController = navController,
                                currentTheme = themeSetting,
                                onThemeChange = { newTheme ->
                                    themeSetting = newTheme
                                }
                            )
                        }

                        composable(route = "/notifications") {
                            NotificationsScreen(navController = navController)
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
