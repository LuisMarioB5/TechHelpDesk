package dev.boni.techhelpdesk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import dev.boni.techhelpdesk.R
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.boni.techhelpdesk.data.local.SessionPreferences
import dev.boni.techhelpdesk.data.local.SettingsPreferences
import dev.boni.techhelpdesk.data.repository.AuthRepository
import dev.boni.techhelpdesk.ui.auth.checkBiometricAvailability
import kotlinx.coroutines.delay

/**
 * Pantalla de bienvenida (Splash) que maneja la lógica de sesión y navegación automática.
 *
 * @param modifier Modificador de Compose.
 * @param navController Controlador de navegación para redirigir al usuario.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current

    val authRepo = remember { AuthRepository() }
    val sessionPrefs = remember { SessionPreferences(context) }
    val settingsPrefs = remember { SettingsPreferences(context) }

    val hasActiveSession = authRepo.isSessionActive()
    val wantsToRemember = sessionPrefs.shouldRememberMe()

    val isBiometricHardwareAvailable = remember { checkBiometricAvailability(context) }
    val userWantBiometricLogin = settingsPrefs.isBiometricEnabled()
    val canUseBiometrics = isBiometricHardwareAvailable && userWantBiometricLogin

    val navigateToLogin = {
        navController.navigate("/login") {
            popUpTo("/splash") { inclusive = true }
        }
    }

    val navigateToRegister = {
        navController.navigate("/register") {
            popUpTo("/splash") { inclusive = true }
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)

        if (!wantsToRemember) {
            authRepo.signOut(context)
            return@LaunchedEffect
        }

        if (!hasActiveSession) {
            return@LaunchedEffect
        }

        if (canUseBiometrics) {
            navigateToLogin();
        } else {
            navController.navigate("/dashboard") {
                popUpTo("/splash") { inclusive = true }
            }
        }
    }

    SplashContent(
        modifier = modifier,
        onNavigateToLogin = navigateToLogin,
        onNavigateToRegister = navigateToRegister
    )
}

/**
 * Contenido visual de la pantalla Splash (Stateless).
 * Se separa para facilitar el Preview y las pruebas de UI.
 */
@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var isAnimating by remember { mutableStateOf(true) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        delay(1500)
        isAnimating = false
    }

    val logoScale by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), label = "logoAlpha"
    )
    val buttonsOffsetY by animateDpAsState(
        targetValue = if (isAnimating) 32.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000, delayMillis = 300), label = "buttonsOffsetY"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 300), label = "buttonsAlpha"
    )
    val versionAlpha by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 500), label = "versionAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            )
            .padding(24.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_techhelpdesk),
                            contentDescription = stringResource(R.string.cd_app_logo),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.cd_check_circle),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = stringResource(R.string.splash_slogan),
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = with(density) { buttonsOffsetY.toPx() }
                        alpha = buttonsAlpha
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 8.dp)
                ) {
                    Text(stringResource(R.string.splash_btn_login), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Text(stringResource(R.string.splash_btn_register), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.splash_version_template, stringResource(R.string.app_version)),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.graphicsLayer { alpha = versionAlpha }
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF1E40AF)
@Composable
fun SplashScreenPreview() {
    TechHelpDeskTheme {
        SplashContent()
    }
}