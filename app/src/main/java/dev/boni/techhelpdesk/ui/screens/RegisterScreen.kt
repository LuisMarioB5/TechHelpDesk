package dev.boni.techhelpdesk.ui.screens // O el paquete correcto

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.* // Importar todos
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler // Para abrir enlaces (simulado)
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.MobileButton // Reutilizamos MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant // Enum de MobileButton
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import androidx.compose.runtime.rememberCoroutineScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // --- Estado del Formulario ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }

    // Estado para Errores ---
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Validación en tiempo real ---
    val isEmailValid by remember {
        derivedStateOf {
            // El email es válido si está vacío O si cumple el patrón
            email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
    val doPasswordsMatch by remember {
        derivedStateOf {
            // Las contraseñas coinciden si la confirmación está vacía O si son iguales
            confirmPassword.isBlank() || password == confirmPassword
        }
    }

    val scope = rememberCoroutineScope()
    val authRepo = remember { AuthRepository() }

    // Función de Validación Completa ---
    val validateRegisterForm: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        // Nombre
        if (name.isBlank()) {
            newErrors["name"] = "El nombre es requerido"
        }
        // Email
        if (email.isBlank()) {
            newErrors["email"] = "El correo es requerido"
        } else if (!isEmailValid) {
            newErrors["email"] = "Formato de correo inválido"
        }
        // Contraseña
        if (password.isBlank()) {
            newErrors["password"] = "La contraseña es requerida"
        } else if (password.length < 6) {
            newErrors["password"] = "La contraseña debe tener al menos 6 caracteres"
        }
        // Confirmar Contraseña
        if (confirmPassword.isBlank()) {
            newErrors["confirmPassword"] = "Confirma la contraseña"
        } else if (!doPasswordsMatch) {
            newErrors["confirmPassword"] = "Las contraseñas no coinciden"
        }
        // Términos
        if (!acceptTerms) {
            newErrors["acceptTerms"] = "Debes aceptar los términos"
        }

        errors = newErrors // Actualiza el estado de errores
        newErrors.isEmpty() // Devuelve true si NO hay errores
    }

    // Lógica de Registro con Validación ---
    val handleRegister = {
        if (validateRegisterForm()) { // Llama a la validación
            scope.launch {
                val result = authRepo.registerUser(name, email, password)
                // ¡Éxito! Navega al Dashboard
                if (result.isSuccess) {
                    navController.navigate("/dashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    // Muestra el error de Firebase (ej. "email ya en uso")
                    errors = errors + ("email" to (result.exceptionOrNull()?.message ?: "Error desconocido"))
                }
            }
        }
    }

    val handleSocialRegister = { provider: String ->
        println("Registering with $provider") // Simulación
        navController.navigate("/dashboard") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
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
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .offset(x = (-8).dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                Text("Únete a TechHelpDesk hoy", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
            }

            // --- Formulario y Contenido ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errors = errors - "name" }, // Limpia error
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo *") },
                    placeholder = { Text("María López") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    isError = errors.containsKey("name"), // Muestra error
                    supportingText = { FormFieldErrorText(error = errors["name"]) } // Muestra mensaje
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errors = errors - "email" }, // Limpia error
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico *") },
                    placeholder = { Text("tu@email.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    // Error si está en el map O si el formato en tiempo real es inválido
                    isError = errors.containsKey("email") || !isEmailValid,
                    supportingText = {
                        val realTimeError = if (!isEmailValid) "Formato de correo inválido" else null
                        FormFieldErrorText(error = errors["email"] ?: realTimeError)
                    }
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errors = errors - "password" }, // Limpia error
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña *") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    singleLine = true,
                    isError = errors.containsKey("password"),
                    supportingText = {
                        FormFieldErrorText(error = errors["password"], defaultText = "Mínimo 6 caracteres")
                    }
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errors = errors - "confirmPassword" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmar contraseña *") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    // Error si está en el map O si no coincide en tiempo real
                    isError = errors.containsKey("confirmPassword") || !doPasswordsMatch,
                    supportingText = {
                        val realTimeError = if (!doPasswordsMatch) "Las contraseñas no coinciden" else null
                        FormFieldErrorText(error = errors["confirmPassword"] ?: realTimeError)
                    }
                )

                // Accept Terms
                Column { // Envolvemos en Column para poner el error debajo
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = acceptTerms,
                            onCheckedChange = { acceptTerms = it; errors = errors - "acceptTerms" },
                            modifier = Modifier.padding(top = 0.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        AcceptTermsText()
                    }
                    // Mostrar error de términos
                    FormFieldErrorText(error = errors["acceptTerms"], modifier = Modifier.padding(start = 16.dp))
                }

                // Register Button
                MobileButton(
                    onClick = handleRegister, // Llama a la función con validación
                    variant = MobileButtonVariant.FILLED,
                    fullWidth = true,
                    enabled = true, // El botón siempre está habilitado, la validación se encarga
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Crear cuenta")
                }

                // Divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text("O regístrate con", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Social Register Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Google
                    Button(
                        onClick = { handleSocialRegister("Google") },
                        modifier = Modifier.fillMaxWidth().height(56.dp), // h-14
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface) // bg-white
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_google),
                            contentDescription = "Google Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Continuar con Google", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    // Microsoft
                    Button(
                        onClick = { handleSocialRegister("Microsoft") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_windows),
                            contentDescription = "Microsoft Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Continuar con Microsoft", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    // Apple
                    Button(
                        onClick = { handleSocialRegister("Apple") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_apple),
                            contentDescription = "Apple Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Continuar con Apple", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Login Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text("¿Ya tienes una cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { navController.navigate("/login") }) {
                        Text("Inicia sesión")
                    }
                }
            } // Fin Column principal del formulario
        } // Fin Column scrollable
    } // Fin Scaffold
}

@Composable
fun FormFieldErrorText(error: String?, modifier: Modifier = Modifier, defaultText: String? = null) {
    val errorColor = MaterialTheme.colorScheme.error
    // Usamos un Box con altura mínima para reservar espacio y evitar saltos
    Box(modifier = modifier.heightIn(min = 16.dp)) {
        val textToShow = error ?: defaultText
        if (textToShow != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Muestra el icono solo si es un error real
                if (error != null) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        tint = errorColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = textToShow,
                    // Color de error si hay error, si no, color de texto de ayuda normal
                    color = if (error != null) errorColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Helper Composable para el texto de Términos y Condiciones (CORREGIDO)
@OptIn(ExperimentalTextApi::class)
@Composable
fun AcceptTermsText(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        append("Acepto los ")

        pushLink(LinkAnnotation.Clickable(
            tag = "terms",
            linkInteractionListener = {
                println("Clicked Terms")
            }
        ))
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline)
        ) {
            append("términos y condiciones")
        }
        pop()

        append(" y la ")

        pushLink(LinkAnnotation.Clickable(
            tag = "privacy",
            linkInteractionListener = {
                println("Clicked Privacy")
                // uriHandler.openUri("https://example.com/privacy") // URL real
            }
        ))
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline)
        ) {
            append("política de privacidad")
        }
        pop()
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    )
}
// --- Preview ---
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TechHelpDeskTheme {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}