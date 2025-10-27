// SplashScreen.kt

package dev.boni.techhelpdesk.ui.screens // O el paquete donde quieras poner tus pantallas

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SupportAgent // Icono equivalente
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme // Asegúrate de importar tu tema
import kotlinx.coroutines.delay

// --- Pantalla Splash ---
/**
 * Pantalla de bienvenida con animación de entrada.
 *
 * @param modifier Modificador de Compose.
 * @param onNavigateToLogin Lambda a ejecutar para navegar a la pantalla de Login.
 * @param onNavigateToRegister Lambda a ejecutar para navegar a la pantalla de Registro.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Estado para controlar la animación
    var isAnimating by remember { mutableStateOf(true) }

    val density = LocalDensity.current

    // Efecto para cambiar el estado después de un retraso (como el setTimeout)
    LaunchedEffect(Unit) {
        delay(1500) // Espera 1.5 segundos
        isAnimating = false
    }

    // Valores animados
    val logoScale by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), label = "logoAlpha"
    )
    val buttonsOffsetY by animateDpAsState(
        targetValue = if (isAnimating) 32.dp else 0.dp, // 8 * 4dp = 32dp approx translate-y-8
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
                Brush.verticalGradient( // bg-gradient-to-b
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer // Asumiendo primaryContainer como el color final
                    )
                )
            )
            // Padding general y para barras del sistema si usas edge-to-edge
            .padding(24.dp) // p-6
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Empuja botones abajo
        ) {
            // Sección del Logo (ocupa el espacio central)
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio disponible (flex-1)
                    .graphicsLayer { // Aplicar animación de escala y opacidad
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Box(contentAlignment = Alignment.Center) {
                    // Cuadrado blanco principal
                    Box(
                        modifier = Modifier
                            .size(96.dp) // w-24 h-24
                            .shadow(16.dp, RoundedCornerShape(24.dp)) // shadow-2xl, rounded-3xl approx
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SupportAgent,
                            contentDescription = "Logo TechHelpDesk",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp) // text-5xl approx
                        )
                    }
                    // Círculo pequeño con check
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // absolute -bottom-2 -right-2
                            .offset(x = 8.dp, y = 8.dp) // Ajuste fino para la posición
                            .size(40.dp) // w-10 h-10
                            .shadow(4.dp, CircleShape) // shadow-lg
                            .clip(CircleShape)
                            // Usamos secondary como color de acento, ajústalo si tienes otro
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp) // text-2xl
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp)) // mb-6

                Text(
                    text = "TechHelpDesk",
                    fontSize = 36.sp, // text-4xl
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp) // mb-3
                )
                Text(
                    text = "Tu soporte técnico, siempre a mano",
                    fontSize = 18.sp, // text-lg
                    color = Color.White.copy(alpha = 0.9f), // text-white/90
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp) // max-w-xs approx
                )
            }

            // Sección de Botones y Versión (abajo)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { // Aplicar animación de traslación y opacidad
                        translationY = with(density) { buttonsOffsetY.toPx() }
                        alpha = buttonsAlpha
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp) // space-y-3
            ) {
                // Botón Iniciar Sesión (Usando MobileButton si existe, si no, Button normal)
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // h-14
                    shape = RoundedCornerShape(12.dp), // rounded-xl
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, // bg-white
                        contentColor = MaterialTheme.colorScheme.primary // text-[var(--color-primary)]
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 8.dp) // shadow-lg hover:shadow-xl
                ) {
                    Text("Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Medium) // text-lg font-medium
                }

                // Botón Registrarse (Usando MobileButton si existe, si no, OutlinedButton normal)
                OutlinedButton( // O usa MobileButton(variant = MobileButtonVariant.OUTLINED, ...)
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // h-14
                    shape = RoundedCornerShape(12.dp), // rounded-xl
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White // text-white
                    ),
                    border = BorderStroke(2.dp, Color.White) // border-2 border-white
                ) {
                    Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Medium) // text-lg font-medium
                }

                Spacer(modifier = Modifier.height(12.dp)) // mt-6 (approx between button and version)

                // Versión
                Text(
                    text = "Versión 1.0.0",
                    fontSize = 12.sp, // text-sm
                    color = Color.White.copy(alpha = 0.7f), // text-white/70
                    modifier = Modifier.graphicsLayer { alpha = versionAlpha } // Animación de opacidad
                )
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, backgroundColor = 0xFF1E40AF) // Fondo azul para ver el blanco
@Composable
fun SplashScreenPreview() {
    TechHelpDeskTheme {
        SplashScreen(onNavigateToLogin = {}, onNavigateToRegister = {})
    }
}