package dev.boni.techhelpdesk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.boni.techhelpdesk.ui.components.BottomNavigation
import dev.boni.techhelpdesk.ui.theme.LightCustomColors
import dev.boni.techhelpdesk.ui.theme.LocalCustomColors
import dev.boni.techhelpdesk.ui.theme.TechHelpDeskTheme

// --- Pantalla de Perfil ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
) {
    // --- Estado ---
    var biometricEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("es") }
    var selectedTheme by remember { mutableStateOf("system") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- Lógica ---
    val handleLogout = {
        showLogoutDialog = true
    }

    // --- Diálogo de Confirmación de Logout ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("auth_graph") { // Asumiendo que "auth_graph" es la ruta de tu grafo de login/splash
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ProfileHeader(
                name = "Luis Rodríguez",
                email = "luis.rodriguez@empresa.com",
                role = "Usuario estándar",
                onEditClick = { /* Lógica para editar perfil */ }
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
            // --- Item 1: Notificaciones ---
            item {
                SettingsClickableItem(
                    label = "Notificaciones",
                    subtext = "Ver todas las notificaciones",
                    icon = Icons.Default.Notifications,
                    badgeCount = 3,
                    onClick = { navController.navigate("/notifications") }
                )
            }

            // Conocimiento (Preguntas frecuentes)
            item {
                SettingsClickableItem(
                    label = "Conocimiento",
                    subtext = "Ver preguntas frecuentes",
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    onClick = { navController.navigate("/knowledge") }
                )
            }

            // --- Item 2: Idioma (Dropdown) ---
            item {
                val languageOptions = mapOf("es" to "Español", "en" to "English", "pt" to "Português")
                SettingsDropdownItem(
                    label = "Idioma",
                    icon = Icons.Default.Language,
                    options = languageOptions,
                    selectedKey = selectedLanguage,
                    onSelectionChange = { selectedLanguage = it }
                )
            }

            // --- Item 3: Biométrico (Toggle) ---
            item {
                SettingsToggleItem(
                    label = "Inicio biométrico",
                    subtext = "Usar huella digital para acceder",
                    icon = Icons.Default.Fingerprint,
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
            }

            // --- Item 4: Notificaciones Push (Toggle) ---
            item {
                SettingsToggleItem(
                    label = "Notificaciones push",
                    subtext = "Recibir alertas y actualizaciones",
                    icon = Icons.Default.NotificationsActive,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            // --- Item 5: Tema Oscuro (Dropdown - CAMBIO) ---
            item {
                val themeOptions = mapOf("system" to "Sistema", "light" to "Claro", "dark" to "Oscuro")
                val themeIcon = when(currentTheme) {
                    "light" -> Icons.Default.LightMode
                    "dark" -> Icons.Default.DarkMode
                    else -> Icons.Default.SettingsBrightness // "system"
                }
                SettingsDropdownItem(
                    label = "Tema oscuro",
                    icon = themeIcon,
                    options = themeOptions,
                    selectedKey = currentTheme,
                    onSelectionChange = onThemeChange
                )
            }

            // --- Item 6: Política de Privacidad ---
            item {
                SettingsClickableItem(
                    label = "Política de privacidad",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* Navegar a Política de Privacidad */ }
                )
            }

            // --- Item 7: Cerrar Sesión ---
            item {
                SettingsClickableItem(
                    label = "Cerrar sesión",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    color = MaterialTheme.colorScheme.error,
                    onClick = handleLogout
                )
            }
        } // Fin LazyColumn
    } // Fin Scaffold
}


// --- Componentes Helper para la UI de Perfil (en el mismo archivo) ---

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
                    contentDescription = "Avatar",
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
                Icon(Icons.Filled.Edit, contentDescription = "Editar perfil", modifier = Modifier.size(16.dp))
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

@OptIn(ExperimentalMaterial3Api::class) // Para BadgedBox
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
                contentDescription = null,
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
    options: Map<String, String>, // <Key, Label>
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


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TechHelpDeskTheme {
        CompositionLocalProvider(LocalCustomColors provides LightCustomColors) {
            val navController = rememberNavController()
            var previewTheme by remember { mutableStateOf("system") }
            ProfileScreen(
                navController = navController,
                currentTheme = previewTheme,
                onThemeChange = { previewTheme = it }
            )
        }
    }
}