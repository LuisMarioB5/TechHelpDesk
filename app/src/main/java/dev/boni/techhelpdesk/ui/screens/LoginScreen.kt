package dev.boni.techhelpdesk.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme
import androidx.compose.runtime.rememberCoroutineScope
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.launch
import dev.boni.techhelpdesk.ui.auth.authenticateWithBiometric
import androidx.compose.runtime.LaunchedEffect
import dev.boni.techhelpdesk.ui.auth.checkBiometricAvailability
import dev.boni.techhelpdesk.data.local.SessionPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Dependencias
    val authRepo = remember { AuthRepository() }
    val sessionPrefs = remember { SessionPreferences(context) }

    val hasActiveSession = authRepo.isSessionActive()
    val wantsToRemember = sessionPrefs.shouldRememberMe()

    // --- Estado del Formulario ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(sessionPrefs.shouldRememberMe()) }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // --- Flag para mostrar/ocultar los botones de inicio de sesión con Microsoft y Apple ---
    val showExtraProviders = false
    
    // Estado para saber si el botón biométrico debe mostrarse
    var isBiometricHardwareAvailable by remember { mutableStateOf(false) }

    // --- LÓGICA DE NAVEGACIÓN EXITOSA ---
    val navigateToDashboard = {
        navController.navigate("/dashboard") {
            popUpTo("/login") { inclusive = true } // Limpiamos el login del historial
            launchSingleTop = true
        }
    }

    // --- LÓGICA DE LOGIN BIOMÉTRICO ---
    val triggerBiometricLogin = {
        authenticateWithBiometric(
            context = context,
            onSuccess = {
                navigateToDashboard()
            },
            onError = { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- EFECTO DE INICIO (Auto-Login y Chequeo de Hardware) ---
    LaunchedEffect(Unit) {
        isBiometricHardwareAvailable = checkBiometricAvailability(context)

        if (hasActiveSession) {
            if (wantsToRemember) {
                if (isBiometricHardwareAvailable) {
                    triggerBiometricLogin()
                } else {
                    navigateToDashboard()
                }
            } else {
                authRepo.signOut()
            }
        }
    }

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

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.my_google_auth_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                scope.launch {
                    val authResult = authRepo.signInWithCredential(credential)
                    if (authResult.isSuccess) {
                        sessionPrefs.setRememberMe(rememberMe)
                        navigateToDashboard()
                    } else {
                        Toast.makeText(context, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign In falló: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    // --- MANEJADORES DE BOTONES ---
     val handleLogin = {
        if (validateLogin()) {
            sessionPrefs.setRememberMe(rememberMe)

            scope.launch {
                val result = authRepo.loginUser(email, password)
                if (result.isSuccess) {
                    navigateToDashboard()
                } else {
                    errors = errors + ("password" to "Credenciales incorrectas")
                }
            }
        }
    }

    val handleSocialLogin = { provider: String ->
        sessionPrefs.setRememberMe(rememberMe)

        when (provider) {
            "Google" -> {
                googleLauncher.launch(googleSignInClient.signInIntent)
            }
//            "Microsoft" -> {
//            }
//            "Apple" -> {
//            }
            "Biometric" -> triggerBiometricLogin()
        }
    }

    // --- UI ---
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
            // --- Header ---
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // --- Formulario y Contenido ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Iniciar sesión")
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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
                        modifier = Modifier.padding(horizontal = 16.dp),
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // --- GOOGLE ---
                    Button(
                        onClick = { handleSocialLogin("Google") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
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

                    if(showExtraProviders) {

                        // --- MICROSOFT ---
                        Button(
                            onClick = { handleSocialLogin("Microsoft") },
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
                        // --- APPLE ---
                        Button(
                            onClick = { handleSocialLogin("Apple") },
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
                    // --- BOTÓN BIOMÉTRICO CONDICIONAL ---
                    if (isBiometricHardwareAvailable && hasActiveSession && wantsToRemember) {
                        Button(
                            onClick = { handleSocialLogin("Biometric") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                Icons.Filled.Fingerprint,
                                contentDescription = "Biometric",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Usar huella digital", color = MaterialTheme.colorScheme.onSurface)
                        }
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