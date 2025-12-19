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
import androidx.compose.ui.res.stringResource
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.data.local.SettingsPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepo = remember { AuthRepository() }
    val sessionPrefs = remember { SessionPreferences(context) }
    val settingsPreferences = remember { SettingsPreferences(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(sessionPrefs.shouldRememberMe()) }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val isBiometricHardwareAvailable = remember {
        checkBiometricAvailability(context)
    }
    val hasActiveSession = authRepo.isSessionActive()
    val wantsToRemember = sessionPrefs.shouldRememberMe()
    val userWantBiometricLogin = settingsPreferences.isBiometricEnabled()
    val canUseBiometrics = isBiometricHardwareAvailable && userWantBiometricLogin

    val showExtraProviders = false


    val navigateToDashboard = {
        navController.navigate("/dashboard") {
            popUpTo("/login") { inclusive = true }
            launchSingleTop = true
        }
    }

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

    LaunchedEffect(Unit) {
        if (!hasActiveSession) return@LaunchedEffect

        if (!wantsToRemember) {
            authRepo.signOut(context)
            return@LaunchedEffect
        }

        if (canUseBiometrics) {
            triggerBiometricLogin()
        } else {
            navigateToDashboard()
        }
    }

    val emailRequired = stringResource(R.string.alert_email_required)
    val emailInvalid = stringResource(R.string.alert_email_invalid)
    val passwordRequired = stringResource(R.string.alert_password_required)

    val validateLogin: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        if (email.isBlank()) {
            newErrors["email"] = emailRequired
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newErrors["email"] = emailInvalid
        }
        if (password.isBlank()) {
            newErrors["password"] = passwordRequired
        }
        errors = newErrors
        newErrors.isEmpty()
    }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
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
                        val errorMsg = context.getString(R.string.error_google_sign_in, authResult.toString())
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            val errorMsg = context.getString(R.string.error_google_sign_in_failed, e.statusCode)
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    val passwordInvalid = stringResource(R.string.alert_invalid_credentials)
     val handleLogin = {
        if (validateLogin()) {
            sessionPrefs.setRememberMe(rememberMe)

            scope.launch {
                val result = authRepo.loginUser(email, password)
                if (result.isSuccess) {
                    navigateToDashboard()
                } else {
                    errors = errors + ("password" to passwordInvalid)
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
//            "Microsoft" -> { }
//            "Apple" -> { }
            "Biometric" -> triggerBiometricLogin()
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
                    modifier = Modifier.padding(bottom = 16.dp).offset(x = (-8).dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), tint = Color.White)
                }

                Text(
                    stringResource(R.string.dashboard_welcome_back),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errors = errors - "email" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_email_input)) },
                    placeholder = { Text(stringResource(R.string.placeholder_email_input)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = errors.containsKey("email"),
                    supportingText = { FormFieldErrorText(error = errors["email"]) }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errors = errors - "password" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_password_input)) },
                    placeholder = { Text(stringResource(R.string.placeholder_password_input)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.icon_password_input)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) stringResource(R.string.cd_hide_password) else stringResource(R.string.cd_show_password)
                            )
                        }
                    },
                    singleLine = true,
                    isError = errors.containsKey("password"),
                    supportingText = { FormFieldErrorText(error = errors["password"]) }
                )

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
                        Text(stringResource(R.string.checkbox_remember_me), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { navController.navigate("/forgot-password") }) {
                        Text(stringResource(R.string.advice_forgot_password_title), style = MaterialTheme.typography.bodySmall)
                    }
                }

                MobileButton(
                    onClick = handleLogin,
                    variant = MobileButtonVariant.FILLED,
                    fullWidth = true,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.btn_login_action))
                }

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
                        stringResource(R.string.divider_continue_with),
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

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { handleSocialLogin("Google") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_google),
                            contentDescription = stringResource(R.string.cd_google_logo),
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.btn_continue_google), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }

                    if(showExtraProviders) {

                        Button(
                            onClick = { handleSocialLogin("Microsoft") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logo_windows),
                                contentDescription = stringResource(R.string.cd_microsoft_logo),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(stringResource(R.string.btn_continue_microsoft), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { handleSocialLogin("Apple") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logo_apple),
                                contentDescription = stringResource(R.string.cd_apple_logo),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(stringResource(R.string.btn_continue_apple), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (hasActiveSession && wantsToRemember && canUseBiometrics) {
                        Button(
                            onClick = { handleSocialLogin("Biometric") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                Icons.Filled.Fingerprint,
                                contentDescription = stringResource(R.string.cd_biometric_icon),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(stringResource(R.string.btn_continue_biometric), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(stringResource(R.string.text_no_account_question), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { navController.navigate("/register") }) {
                        Text(stringResource(R.string.btn_register_action))
                    }
                }
            }
        }
    }
}

@Composable
fun FormFieldErrorText(error: String?, modifier: Modifier = Modifier) {
    val errorColor = MaterialTheme.colorScheme.error
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TechHelpDeskTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}