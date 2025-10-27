// LoginScreen.kt

package dev.boni.techhelpdesk.ui.screens // O el paquete correcto

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.* // Importar todos
import androidx.compose.material.icons.outlined.* // Importar todos outlined
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.MobileButton // Reutilizamos MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant // Enum de MobileButton
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado del Formulario ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val validateLogin: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        if (email.isBlank()) {
            newErrors["email"] = "El correo es requerido"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newErrors["email"] = "Formato de correo inválido"
        }
        if (password.isBlank()) {
            newErrors["password"] = "La contraseña es requerida"
        }
        errors = newErrors
        newErrors.isEmpty()
    }

    // --- Lógica Simulada ---
    val handleLogin = {
        // --- CAMBIO: Llamar validación ---
        if (validateLogin()) {
            println("Login Válido: $email") // Simulación
            navController.navigate("/dashboard") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            println("Errores de Login: $errors") // Simulación
        }
    }
    val handleSocialLogin = { provider: String ->
        println("Logging in with $provider")
        // No validamos aquí, asumimos que el proveedor social lo hace
        navController.navigate("/dashboard") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background // Fondo principal
    ) { innerPadding -> // Scaffold no aplica padding superior si no hay topBar

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Solo padding inferior del Scaffold
                .verticalScroll(rememberScrollState()) // Hacemos toda la columna scrollable
        ) {
            // --- Header Simple ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding() // Padding para la barra de estado
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp) // px-6 pt-12 pb-8
            ) {
                // Botón Atrás
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(bottom = 16.dp).offset(x = (-8).dp) // mb-6 -ml-2 p-2
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                // Títulos
                Text(
                    "Bienvenido de nuevo",
                    style = MaterialTheme.typography.headlineSmall, // text-3xl approx
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp) // mb-2
                )
                Text(
                    "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyLarge, // text-base
                    color = Color.White.copy(alpha = 0.8f) // text-white/80
                )
            }

            // --- Formulario y Contenido ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp), // px-6 py-8
                verticalArrangement = Arrangement.spacedBy(20.dp) // space-y-5 approx
            ) {
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errors = errors - "email" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("tu@email.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = errors.containsKey("email"),
                    supportingText = { FormFieldErrorText(error = errors["email"]) }
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errors = errors - "password" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    singleLine = true,
                    isError = errors.containsKey("password"),
                    supportingText = { FormFieldErrorText(error = errors["password"]) }
                )

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text("Recordarme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { navController.navigate("/forgot-password") }) {
                        Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Login Button
                MobileButton(
                    onClick = handleLogin,
                    variant = MobileButtonVariant.FILLED,
                    fullWidth = true,
                    modifier = Modifier.padding(top = 16.dp) // mt-6
                ) {
                    Text("Iniciar sesión")
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), // py-4
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "O continúa con",
                        modifier = Modifier.padding(horizontal = 16.dp), // px-4
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Social Login Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // space-y-3
                    // Google
                    OutlinedButton(
                        onClick = { handleSocialLogin("Google") },
                        modifier = Modifier.fillMaxWidth().height(56.dp), // h-14
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White) // bg-white
                    ) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Google Logo", tint = Color.Unspecified, modifier = Modifier.size(24.dp)) // Usar icono de Google real o SVG
                        Spacer(Modifier.width(12.dp))
                        Text("Continuar con Google", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    // Microsoft
                    OutlinedButton(
                        onClick = { handleSocialLogin("Microsoft") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Window, contentDescription = "Microsoft Logo", tint = Color(0xFF5E5E5E), modifier = Modifier.size(24.dp)) // Usar icono real o SVG
                        Spacer(Modifier.width(12.dp))
                        Text("Continuar con Microsoft", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
//                    // Apple
//                    Button(
//                        onClick = { handleSocialLogin("Apple") },
//                        modifier = Modifier.fillMaxWidth().height(56.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White) // bg-black text-white
//                    ) {
//                        Icon(Icons.Filled.Apple, contentDescription = "Apple Logo", tint = Color.Unspecified, modifier = Modifier.size(24.dp)) // Usar icono real o SVG
//                        Spacer(Modifier.width(12.dp))
//                        Text("Continuar con Apple", fontWeight = FontWeight.Medium)
//                    }
                    // Biometric
                    Button( // Usar Button o OutlinedButton según prefieras
                        onClick = { handleSocialLogin("Biometric") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // bg-[var(--color-surface-variant)]
                    ) {
                        Icon(Icons.Filled.Fingerprint, contentDescription = "Biometric", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) // text-3xl approx
                        Spacer(Modifier.width(12.dp))
                        Text("Usar huella digital", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Sign Up Link
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp), // mt-8
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text("¿No tienes una cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { navController.navigate("/register") }) {
                        Text("Regístrate")
                    }
                }
            }
        }
    }
}

@Composable
fun FormFieldErrorText(error: String?, modifier: Modifier = Modifier) {
    val errorColor = MaterialTheme.colorScheme.error
    // Usamos un Box con altura mínima para reservar espacio y evitar saltos
    Box(modifier = modifier.heightIn(min = 16.dp)) {
        if (error != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Error, contentDescription = null, tint = errorColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = error, color = errorColor, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TechHelpDeskTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}