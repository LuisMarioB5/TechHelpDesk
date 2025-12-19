package dev.boni.techhelpdesk.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.R
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.screens.viewmodels.ProfileViewModel
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()


    var biometricEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val handleLogout = {
        showLogoutDialog = true
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.profile_title_logout)) },
            text = { Text(stringResource(R.string.dialog_logout_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.onConfirmLogout(
                            context,
                            onSuccess = {
                                navController.navigate("/login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )

                    }
                ) {
                    Text(stringResource(R.string.profile_title_logout), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ProfileHeader(
                name = uiState.name,
                email = uiState.email,
                role = stringResource(id = uiState.roleResId),
                onEditClick = { navController.navigate("/profile/edit") }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                top = 24.dp,
                bottom = 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsClickableItem(
                    label = stringResource(R.string.profile_title_notifications),
                    subtext = stringResource(R.string.profile_subtext_notifications),
                    icon = Icons.Default.Notifications,
                    badgeCount = 3,
                    onClick = { navController.navigate("/notifications") }
                )
            }

            item {
                SettingsClickableItem(
                    label = stringResource(R.string.profile_title_knowledge),
                    subtext = stringResource(R.string.profile_subtext_knowledge),
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    onClick = { navController.navigate("/knowledge") }
                )
            }

            item {
                val languageOptions = mapOf(
                    "system" to stringResource(R.string.option_lang_system),
                    "es" to stringResource(R.string.option_lang_es),
                    "en" to stringResource(R.string.option_lang_en)
                )

                SettingsDropdownItem(
                    label = stringResource(R.string.profile_title_language),
                    icon = Icons.Default.Language,
                    options = languageOptions,
                    selectedKey = currentLanguage,
                    onSelectionChange = onLanguageChange
                )
            }

            item {
                SettingsToggleItem(
                    label = stringResource(R.string.profile_title_biometric),
                    subtext = stringResource(R.string.profile_subtext_biometric),
                    icon = Icons.Default.Fingerprint,
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
            }

            item {
                SettingsToggleItem(
                    label = stringResource(R.string.profile_title_push),
                    subtext = stringResource(R.string.profile_subtext_push),
                    icon = Icons.Default.NotificationsActive,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            item {
                val themeOptions = mapOf(
                    "system" to stringResource(R.string.option_theme_system),
                    "light" to stringResource(R.string.option_theme_light),
                    "dark" to stringResource(R.string.option_theme_dark)
                )

                val themeIcon = when(currentTheme) {
                    "light" -> Icons.Default.LightMode
                    "dark" -> Icons.Default.DarkMode
                    else -> Icons.Default.SettingsBrightness
                }

                SettingsDropdownItem(
                    label = stringResource(R.string.profile_title_theme),
                    icon = themeIcon,
                    options = themeOptions,
                    selectedKey = currentTheme,
                    onSelectionChange = onThemeChange
                )
            }

            item {
                SettingsClickableItem(
                    label = stringResource(R.string.profile_title_privacy),
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* Navegar a Política de Privacidad */ }
                )
            }

            item {
                SettingsClickableItem(
                    label = stringResource(R.string.profile_title_logout),
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    color = MaterialTheme.colorScheme.error,
                    onClick = handleLogout
                )
            }
        }
    }
}


@Composable
fun ProfileHeader(
    name: String,
    email: String,
    role: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(96.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = stringResource(R.string.cd_avatar_image),
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit_profile), modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(email, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        Spacer(Modifier.height(12.dp))
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White
        ) {
            Text(
                text = role,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsClickableItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    badgeCount: Int = 0,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        contentColor = color
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        if (badgeCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                            ) {
                                Text("$badgeCount", fontSize = 8.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if(color != MaterialTheme.colorScheme.error) MaterialTheme.colorScheme.primary else color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                if (subtext != null) {
                    Text(
                        subtext,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.cd_arrow_right_icon),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    label: String,
    subtext: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Text(
                    subtext,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                thumbContent = {
                    if (checked) Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownItem(
    label: String,
    icon: ImageVector,
    options: Map<String, String>,
    selectedKey: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            }

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                OutlinedTextField(
                    value = options[selectedKey] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent
                    )
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    options.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onSelectionChange(key)
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            val previewViewModel: ProfileViewModel = viewModel()

            var previewTheme by remember { mutableStateOf("system") }
            var previewLang by remember { mutableStateOf("es") }

            ProfileScreen(
                navController = navController,
                viewModel = previewViewModel,
                currentTheme = previewTheme,
                onThemeChange = { previewTheme = it },
                currentLanguage = previewLang,
                onLanguageChange = { previewLang = it }
            )
        }
    }
}