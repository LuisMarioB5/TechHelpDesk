package dev.boni.techhelpdesk.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.MobileButton
import dev.boni.techhelpdesk.ui.components.MobileButtonVariant
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = remember { AuthRepository() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }

    val showExtraProviders = false

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val isEmailValid by remember {
        derivedStateOf {
            email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
    val doPasswordsMatch by remember {
        derivedStateOf {
            confirmPassword.isBlank() || password == confirmPassword
        }
    }

    val validateRegisterForm: () -> Boolean = {
        val newErrors = mutableMapOf<String, String>()

        if (name.isBlank()) {
            newErrors["name"] = context.getString(R.string.alert_name_required)
        }
        if (email.isBlank()) {
            newErrors["email"] = context.getString(R.string.alert_email_required)
        } else if (!isEmailValid) {
            newErrors["email"] = context.getString(R.string.alert_email_invalid)
        }
        if (password.isBlank()) {
            newErrors["password"] = context.getString(R.string.alert_password_required)
        } else if (password.length < 6) {
            newErrors["password"] = context.getString(R.string.alert_password_length)
        }
        if (confirmPassword.isBlank()) {
            newErrors["confirmPassword"] = context.getString(R.string.alert_confirm_password_required)
        } else if (!doPasswordsMatch) {
            newErrors["confirmPassword"] = context.getString(R.string.alert_passwords_match)
        }
        if (!acceptTerms) {
            newErrors["acceptTerms"] = context.getString(R.string.alert_terms_required)
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
                        navController.navigate("/dashboard") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        val errorMsg = context.getString(R.string.error_google_register)
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            val errorMsg = context.getString(R.string.error_google_register_failed, e.statusCode)
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    val handleRegister = {
        if (validateRegisterForm()) {
            scope.launch {
                val result = authRepo.registerUser(name, email, password)
                if (result.isSuccess) {
                    navController.navigate("/dashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    val errorMsg = context.getString(R.string.error_unknown)
                    errors = errors + ("email" to (result.exceptionOrNull()?.message ?: errorMsg))
                }
            }
        }
    }

    val handleSocialRegister = { provider: String ->
        when (provider) {
            "Google" -> {
                googleSignInClient.signOut().addOnCompleteListener {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }
            }
//            "Microsoft" -> { }
//            "Apple" -> { }
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
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .offset(x = (-8).dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_return_icon),
                        tint = Color.White
                    )
                }
                Text(
                    stringResource(R.string.register_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    stringResource(R.string.register_subtitle),
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
                    value = name,
                    onValueChange = { name = it; errors = errors - "name" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_name_input)) },
                    placeholder = { Text(stringResource(R.string.placeholder_name_input)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.cd_person_icon)) },
                    singleLine = true,
                    isError = errors.containsKey("name"),
                    supportingText = { FormFieldErrorText(error = errors["name"]) }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errors = errors - "email" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_email_input)) },
                    placeholder = { Text(stringResource(R.string.placeholder_email_input)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = stringResource(R.string.icon_email_input)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = errors.containsKey("email") || !isEmailValid,
                    supportingText = {
                        val realTimeError = if (!isEmailValid) stringResource(R.string.alert_email_invalid) else null
                        FormFieldErrorText(error = errors["email"] ?: realTimeError)
                    }
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
                    supportingText = {
                        FormFieldErrorText(
                            error = errors["password"],
                            defaultText = stringResource(R.string.helper_password_min_length)
                        )
                    }
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errors = errors - "confirmPassword" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_confirm_password_input)) },
                    placeholder = { Text(stringResource(R.string.placeholder_password_input)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription =  stringResource(R.string.icon_password_input)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = errors.containsKey("confirmPassword") || !doPasswordsMatch,
                    supportingText = {
                        val realTimeError = if (!doPasswordsMatch) stringResource(R.string.alert_passwords_match) else null
                        FormFieldErrorText(error = errors["confirmPassword"] ?: realTimeError)
                    }
                )

                Column {
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
                    FormFieldErrorText(error = errors["acceptTerms"], modifier = Modifier.padding(start = 16.dp))
                }

                MobileButton(
                    onClick = handleRegister,
                    variant = MobileButtonVariant.FILLED,
                    fullWidth = true,
                    enabled = true,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.btn_create_account))
                }

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
                    Text(
                        stringResource(R.string.divider_register_with),
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
                        onClick = { handleSocialRegister("Google") },
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
                        Text(
                            stringResource(R.string.btn_continue_google),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if(showExtraProviders) {
                        Button(
                            onClick = { handleSocialRegister("Microsoft") },
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
                            onClick = { handleSocialRegister("Apple") },
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
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(stringResource(R.string.text_has_account_question), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { navController.navigate("/login") }) {
                        Text(stringResource(R.string.btn_login_link))
                    }
                }
            }
        }
    }
}

@Composable
fun FormFieldErrorText(error: String?, modifier: Modifier = Modifier, defaultText: String? = null) {
    val errorColor = MaterialTheme.colorScheme.error
    Box(modifier = modifier.heightIn(min = 16.dp)) {
        val textToShow = error ?: defaultText
        if (textToShow != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = if (error != null) errorColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun AcceptTermsText(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    val part1 = stringResource(R.string.terms_text_accept)
    val part2Link = stringResource(R.string.terms_text_link_terms)
    val part3 = stringResource(R.string.terms_text_and)
    val part4Link = stringResource(R.string.terms_text_link_privacy)

    val annotatedString = buildAnnotatedString {
        append(part1)

        pushLink(LinkAnnotation.Clickable(
            tag = "terms",
            linkInteractionListener = { println("Clicked Terms") }
        ))
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline)
        ) {
            append(part2Link)
        }
        pop()

        append(part3)

        pushLink(LinkAnnotation.Clickable(
            tag = "privacy",
            linkInteractionListener = { println("Clicked Privacy") }
        ))
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline)
        ) {
            append(part4Link)
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TechHelpDeskTheme {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}