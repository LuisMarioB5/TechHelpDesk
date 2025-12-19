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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val scope = rememberCoroutineScope()
    val authRepo = remember { AuthRepository() }

    val errorEmailRequired = stringResource(R.string.alert_email_required)
    val errorEmailInvalid = stringResource(R.string.alert_email_invalid)

    val validateForm: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()
        if (email.isBlank()) {
            newErrors["email"] = errorEmailRequired
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newErrors["email"] = errorEmailInvalid
        }
        errors = newErrors
        newErrors.isEmpty()
    }

    val errorSendingEmail = stringResource(R.string.alert_error_sending_email)

    val handleResetPassword = {
        if (validateForm()) {
            scope.launch {
                val result = authRepo.recoverPassword(email)
                if (result.isSuccess) {
                    emailSent = true
                } else {
                    errors = errors + ("email" to (result.exceptionOrNull()?.message ?: errorSendingEmail))
                }
            }
        } else {
            println("Errores de validación: $errors")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        if (emailSent) {
            ForgotPasswordSuccessContent(
                innerPadding = innerPadding,
                email = email,
                onSendAgain = { emailSent = false },
                onBackToLogin = { navController.navigate("/login") { popUpTo(navController.graph.startDestinationId) } }
            )
        } else {
            ForgotPasswordFormContent(
                innerPadding = innerPadding,
                email = email,
                onEmailChange = {
                    email = it
                    errors = errors - "email"
                },
                onSubmit = { handleResetPassword() },
                emailError = errors["email"],
                navController = navController
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordFormContent(
    innerPadding: PaddingValues,
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    emailError: String?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val isButtonEnabled by remember(email) {
        derivedStateOf {
            email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    Column(
        modifier = modifier
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
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(x = (-8).dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), tint = Color.White)
            }
            Text(stringResource(R.string.forgot_password_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
            Text(stringResource(R.string.forgot_password_desc), style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.LockReset, contentDescription = stringResource(R.string.advice_forgot_password_icon), tint = MaterialTheme.colorScheme.primary, modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp))
                    Column {
                        Text(stringResource(R.string.advice_forgot_password_title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp), color = MaterialTheme.colorScheme.onSurface)
                        Text(stringResource(R.string.advice_forgot_password_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                    }
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_email_input)) },
                placeholder = { Text(stringResource(R.string.placeholder_email_input)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = stringResource(R.string.icon_email_input)) },
                supportingText = {
                    FormFieldErrorText(
                        error = emailError,
                        defaultText = stringResource(R.string.supporting_text_email_input)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = emailError != null
            )

            MobileButton(
                onClick = onSubmit,
                variant = MobileButtonVariant.FILLED,
                fullWidth = true,
                enabled = email.isNotBlank() && emailError == null,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.send_recovery_link_button))
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Security, contentDescription = stringResource(R.string.advice_security_recovery_icon), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier
                        .padding(end = 12.dp)
                        .size(20.dp))
                    Column {
                        Text(stringResource(R.string.advice_security_recovery_title), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                    }
                }
            }

            TextButton(
                onClick = { navController.navigate("/login"){ popUpTo(navController.graph.startDestinationId)} },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ){
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.return_to_login_button), style = MaterialTheme.typography.bodySmall)
            }

        }
    }
}


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
                onClick = onBackToLogin,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(x = (-8).dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_return_icon), tint = Color.White)
            }
            Text(stringResource(R.string.forgot_password_success_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
            Text(stringResource(R.string.forgot_password_success_desc), style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(LocalCustomColors.current.successContainer.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MarkEmailRead,
                    contentDescription = stringResource(R.string.forgot_password_success_icon),
                    tint = LocalCustomColors.current.success,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.email_success_send_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.email_success_send_desc1))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)) {
                        append(email)
                    }
                    append(stringResource(R.string.email_success_send_desc2))
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp),
                lineHeight = 20.sp
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.exclamation_icon), tint = MaterialTheme.colorScheme.primary, modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp)
                        .padding(top = 2.dp))
                    Column {
                        Text(stringResource(R.string.advice_didnt_received_email_title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                            Text(stringResource(R.string.advice_didnt_received_email_opt1), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(stringResource(R.string.advice_didnt_received_email_opt2), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(stringResource(R.string.advice_didnt_received_email_opt3), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                MobileButton(
                    onClick = onSendAgain,
                    variant = MobileButtonVariant.OUTLINED,
                    fullWidth = true
                ) { Text(stringResource(R.string.send_again_button)) }

                MobileButton(
                    onClick = onBackToLogin,
                    variant = MobileButtonVariant.TEXT,
                    fullWidth = true
                ) { Text(stringResource(R.string.return_to_login_button)) }
            }
        }
    }
}


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
            ForgotPasswordSuccessContent(
                innerPadding = PaddingValues(0.dp),
                email = "usuario@ejemplo.com",
                onSendAgain = {},
                onBackToLogin = {}
            )
        }
    }
}