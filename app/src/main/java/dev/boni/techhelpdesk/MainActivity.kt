package dev.boni.techhelpdesk

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.boni.techhelpdesk.data.local.LanguagePreferences
import dev.boni.techhelpdesk.data.local.ThemePreferences
// import dev.boni.techhelpdesk.utils.LocaleHelper <--- YA NO LO NECESITAS
import dev.boni.techhelpdesk.ui.screens.ConversationsScreen
import dev.boni.techhelpdesk.ui.screens.tickets.create.CreateTicketScreen
import dev.boni.techhelpdesk.ui.screens.DashboardScreen
import dev.boni.techhelpdesk.ui.screens.ForgotPasswordScreen
import dev.boni.techhelpdesk.ui.screens.LoginScreen
import dev.boni.techhelpdesk.ui.screens.NewConversationScreen
import dev.boni.techhelpdesk.ui.screens.NotificationsScreen
import dev.boni.techhelpdesk.ui.screens.profile.ProfileScreen
import dev.boni.techhelpdesk.ui.screens.RegisterScreen
import dev.boni.techhelpdesk.ui.screens.SplashScreen
import dev.boni.techhelpdesk.ui.screens.conversation.ConversationDetailScreen
import dev.boni.techhelpdesk.ui.screens.knowledge.KnowledgeBaseScreen
import dev.boni.techhelpdesk.ui.screens.knowledge.id.KnowledgeArticleScreen
import dev.boni.techhelpdesk.ui.screens.tickets.TicketsScreen
import dev.boni.techhelpdesk.ui.screens.tickets.id.TicketDetailScreen
import dev.boni.techhelpdesk.ui.screens.viewmodels.DashboardViewModel
import dev.boni.techhelpdesk.ui.screens.viewmodels.ProfileViewModel
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import dev.boni.techhelpdesk.ui.screens.profile.edit.EditProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val languagePrefs = LanguagePreferences(this)
        val savedLanguage = languagePrefs.getLanguage()

        enableEdgeToEdge()
        setContent {
            val themePrefs = remember { ThemePreferences(this) }
            var themeSetting by remember { mutableStateOf(themePrefs.getTheme()) }

            var languageSetting by remember { mutableStateOf(savedLanguage) }

            TechHelpDeskTheme(themeSetting = themeSetting) {
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
                            val dashboardViewModel: DashboardViewModel = viewModel()
                            DashboardScreen(navController = navController, viewModel = dashboardViewModel)
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

                        // --- RUTAS DE TICKETS ---
                        composable(
                            route = "/tickets?status={status}",
                            arguments = listOf(
                                navArgument("status") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val initialStatus = backStackEntry.arguments?.getString("status")
                            TicketsScreen(
                                navController = navController,
                                initialFilterStatus = initialStatus
                            )
                        }
                        composable(
                            route = "/ticket/detail/{ticketId}",
                            arguments = listOf(
                                navArgument("ticketId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: "ID_INVALIDO"
                            TicketDetailScreen(navController = navController, ticketId = ticketId)
                        }
                        composable(route = "/ticket/create") {
                            CreateTicketScreen(navController = navController)
                        }

                        // --- RUTAS DE CONOCIMIENTO ---
                        composable(route = "/knowledge") {
                            KnowledgeBaseScreen(navController = navController)
                        }
                        composable(
                            route = "/knowledge/article/{articleId}",
                            arguments = listOf(
                                navArgument("articleId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val articleId = backStackEntry.arguments?.getString("articleId") ?: "ID_INVALIDO"
                            KnowledgeArticleScreen(navController = navController, articleId = articleId)
                        }

                        // --- RUTAS DE PERFIL ---
                        composable(route = "/profile") {
                            val profileViewModel: ProfileViewModel = viewModel()

                            ProfileScreen(
                                navController = navController,
                                viewModel = profileViewModel,
                                // TEMA
                                currentTheme = themeSetting,
                                onThemeChange = { newTheme ->
                                    themeSetting = newTheme
                                    themePrefs.setTheme(newTheme)
                                },
                                // IDIOMA
                                currentLanguage = languageSetting,
                                onLanguageChange = { newLang ->
                                    languageSetting = newLang
                                    languagePrefs.setLanguage(newLang)

                                    val localeList = if (newLang == "system") {
                                        LocaleListCompat.getEmptyLocaleList()
                                    } else {
                                        LocaleListCompat.forLanguageTags(newLang)
                                    }
                                    AppCompatDelegate.setApplicationLocales(localeList)
                                }
                            )
                        }
                        composable(route = "/profile/edit") {
                            val profileViewModel: ProfileViewModel = viewModel()
                            EditProfileScreen(navController = navController, viewModel = profileViewModel)
                        }

                        composable(route = "/notifications") {
                            NotificationsScreen(navController = navController)
                        }

                        // --- RUTAS DE CHAT ---
                        composable(route = "/conversation") {
                            ConversationsScreen(navController = navController)
                        }
                        composable(route = "/conversation/new") {
                            NewConversationScreen(navController = navController)
                        }
                        composable(
                            route = "/conversation/detail/{chatId}",
                            arguments = listOf(
                                navArgument("chatId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId") ?: "ID_INVALIDO"
                            ConversationDetailScreen(navController = navController, chatId = chatId)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Placeholder temporal
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