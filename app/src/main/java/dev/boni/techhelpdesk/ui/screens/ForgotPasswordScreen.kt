package dev.boni.techhelpdesk.ui.screens

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado ---
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) } // Controla qué vista mostrar

    // --- Lógica Simulada ---
    val handleResetPassword = {
        if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            println("Sending password reset email to: $email") // Simulación
            emailSent = true // Cambia al estado de éxito
        } else {
            // Podrías añadir un estado de error aquí si quisieras
            println("Invalid email")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding -> // Scaffold no aplica padding superior si no hay topBar

        // --- Vista Condicional: Formulario o Éxito ---
        if (emailSent) {
            // --- Vista de Éxito ---
            ForgotPasswordSuccessContent(
                innerPadding = innerPadding,
                email = email,
                onSendAgain = { emailSent = false }, // Vuelve al formulario
                onBackToLogin = { navController.navigate("/login") { popUpTo(navController.graph.startDestinationId) } } // Vuelve al login
            )
        } else {
            // --- Vista de Formulario ---
            ForgotPasswordFormContent(
                innerPadding = innerPadding,
                email = email,
                onEmailChange = { email = it },
                onSubmit = handleResetPassword,
                navController = navController
            )
        }
    } // Fin Scaffold
}


// --- Contenido del Formulario ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordFormContent(
    innerPadding: PaddingValues,
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    navController: NavController, // Necesario para "Volver al login"
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()) // Solo padding inferior
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header Simple ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() }, // Botón atrás
                modifier = Modifier.padding(bottom = 16.dp).offset(x = (-8).dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Recuperar contraseña", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
            Text("Te enviaremos un enlace de recuperación", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }

        // --- Formulario ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp), // px-6 py-8
            verticalArrangement = Arrangement.spacedBy(24.dp) // space-y-6 approx
        ) {
            // Info Card 1
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer, // bg-[var(--color-primary-container)]
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 12.dp).size(24.dp))
                    Column {
                        Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        Text("No te preocupes. Ingresa tu correo electrónico...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    }
                }
            }

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") },
                placeholder = { Text("tu@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                supportingText = { Text("Ingresa el correo asociado a tu cuenta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() // Validación simple
            )

            // Reset Button
            MobileButton(
                onClick = onSubmit,
                variant = MobileButtonVariant.FILLED,
                fullWidth = true,
                enabled = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches(), // Habilitar solo si es email válido
                modifier = Modifier.padding(top = 16.dp) // mt-6
            ) {
                Text("Enviar enlace de recuperación")
            }

            // Info Card 2
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 12.dp).size(20.dp))
                    Column {
                        Text("Por tu seguridad, el enlace de recuperación expirará en 24 horas...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                    }
                }
            }

            // Back to Login Link
            TextButton(
                onClick = { navController.navigate("/login"){ popUpTo(navController.graph.startDestinationId)} },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp) // mt-8
            ){
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Volver al inicio de sesión", style = MaterialTheme.typography.bodySmall)
            }

        } // Fin Column Formulario
    } // Fin Column principal scrollable
}


// --- Contenido de la Pantalla de Éxito ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordSuccessContent(
    innerPadding: PaddingValues,
    email: String,
    onSendAgain: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header Simple ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
        ) {
            IconButton(
                // Vuelve al login directamente desde el éxito
                onClick = onBackToLogin,
                modifier = Modifier.padding(bottom = 16.dp).offset(x = (-8).dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Correo enviado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
            Text("Revisa tu bandeja de entrada", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }

        // --- Mensaje de Éxito ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp)) // Espacio superior

            Box(
                modifier = Modifier
                    .size(96.dp) // w-24 h-24
                    .clip(CircleShape)
                    // Usamos success container con alpha bajo
                    .background(LocalCustomColors.current.successContainer.copy(alpha=0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MarkEmailRead, // Icono de correo leído
                    contentDescription = null,
                    tint = LocalCustomColors.current.success, // Color success
                    modifier = Modifier.size(56.dp) // text-5xl
                )
            }
            Spacer(Modifier.height(24.dp)) // mb-6

            Text(
                "¡Correo enviado con éxito!",
                style = MaterialTheme.typography.headlineSmall, // text-2xl approx
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp) // mb-3
            )
            Text(
                text = buildAnnotatedString {
                    append("Hemos enviado un enlace de recuperación a ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)) {
                        append(email)
                    }
                    append(". Por favor, revisa tu bandeja de entrada y sigue las instrucciones.")
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp), // mb-8
                lineHeight = 20.sp // leading-relaxed
            )

            // Info Card Spam
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp) // mb-6
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 12.dp).size(24.dp).padding(top = 2.dp)) // mt-1
                    Column {
                        Text("¿No recibiste el correo?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp)) // mb-2
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)){ // space-y-1
                            Text("• Revisa tu carpeta de spam", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• Verifica que el correo sea correcto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• El enlace expira en 24 horas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }


            // Botones de Acción Éxito
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // space-y-3
            ){
                MobileButton(
                    onClick = onSendAgain,
                    variant = MobileButtonVariant.OUTLINED,
                    fullWidth = true
                ) { Text("Enviar de nuevo") }

                MobileButton(
                    onClick = onBackToLogin,
                    variant = MobileButtonVariant.TEXT,
                    fullWidth = true
                ) { Text("Volver al inicio de sesión") }
            }
        } // Fin Column Éxito
    } // Fin Column principal scrollable
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            ForgotPasswordScreen(navController = navController)
        }
    }
}

@Preview(showBackground = true, name="Forgot Password Success")
@Composable
fun ForgotPasswordSuccessScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            // Simulamos el estado de éxito directamente llamando al Content
            ForgotPasswordSuccessContent(
                innerPadding = PaddingValues(0.dp),
                email = "usuario@ejemplo.com",
                onSendAgain = {},
                onBackToLogin = {}
            )
        }
    }
}