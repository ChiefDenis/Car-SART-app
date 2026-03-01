package com.chiefdenis.carsart.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import com.chiefdenis.carsart.data.repository.ThemeMode
import com.chiefdenis.carsart.data.repository.UserPreferences
import com.chiefdenis.carsart.ui.theme.CarSartMotion
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isContentVisible = true
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    MaterialTheme.colorScheme.surface
                )
            )
        ),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isContentVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = CarSartMotion.tokens.durationMedium4,
                    easing = EaseOutCubic
                )
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = CarSartMotion.tokens.durationMedium4,
                    easing = EaseOutCubic
                ),
                initialOffsetY = { it / 4 }
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = CarSartMotion.tokens.durationShort2
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Theme Settings
                ThemeSettingsCard(
                    themeMode = settings.themeMode,
                    onThemeChange = { viewModel.setThemeMode(it) }
                )

                // Currency Settings
                CurrencySettingsCard(
                    selectedCurrency = settings.currency,
                    onCurrencySelected = { viewModel.setCurrency(it) }
                )

                // Unit System Settings
                UnitSystemSettingsCard(
                    selectedUnitSystem = settings.unitSystem,
                    onUnitSystemSelected = { viewModel.setUnitSystem(it) }
                )

                // Maintenance Settings
                MaintenanceSettingsCard(
                    settings = settings,
                    onMaintenanceRemindersEnabledChange = { viewModel.setMaintenanceRemindersEnabled(it) },
                    onAdvanceWarningDaysChange = { viewModel.setAdvanceWarningDays(it) }
                )

                // Backup and Restore Settings
                BackupAndRestoreCard(
                    onBackup = { viewModel.backup() },
                    onRestore = { viewModel.restore() }
                )
                
                // About Section
                AboutCard()
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ThemeSettingsCard(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    SettingsCard(
        title = "Appearance",
        icon = when (themeMode) {
            ThemeMode.LIGHT -> Icons.Default.LightMode
            ThemeMode.DARK -> Icons.Default.NightsStay
            ThemeMode.SYSTEM -> Icons.Default.Settings
        },
        description = "Choose your preferred theme"
    ) {
        Column(Modifier.fillMaxWidth()) {
            ThemeMode.values().forEach { mode ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChange(mode) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (themeMode == mode),
                        onClick = { onThemeChange(mode) }
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.SYSTEM -> "System"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Light theme"
                                ThemeMode.DARK -> "Dark theme"
                                ThemeMode.SYSTEM -> "System default"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutCard() {
    var showAboutDialog by remember { mutableStateOf(false) }
    
    SettingsCard(
        title = "About",
        icon = Icons.Default.Info,
        description = "App version and information"
    ) {
        Button(
            onClick = { showAboutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "About Car SART",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
    
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // App Icon
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                // App Name and Version
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Car SART",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Version 0.22.0",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                
                // Description
                Text(
                    text = "A comprehensive vehicle maintenance tracking application that helps you manage your car's service history, maintenance schedules, and costs.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencySettingsCard(
    selectedCurrency: AppCurrency,
    onCurrencySelected: (AppCurrency) -> Unit
) {
    SettingsCard(
        title = "Currency Settings",
        icon = Icons.Default.Money,
        description = "Choose your preferred currency for cost display"
    ) {
        CurrencyPicker(
            selectedCurrency = selectedCurrency,
            onCurrencySelected = onCurrencySelected
        )
    }
}

@Composable
fun UnitSystemSettingsCard(
    selectedUnitSystem: AppUnitSystem,
    onUnitSystemSelected: (AppUnitSystem) -> Unit
) {
    SettingsCard(
        title = "Unit System",
        icon = Icons.Default.Speed,
        description = "Select metric or imperial units for measurements"
    ) {
        UnitSystemSelector(
            selectedUnitSystem = selectedUnitSystem,
            onUnitSystemSelected = onUnitSystemSelected
        )
    }
}

@Composable
fun MaintenanceSettingsCard(
    settings: UserPreferences,
    onMaintenanceRemindersEnabledChange: (Boolean) -> Unit,
    onAdvanceWarningDaysChange: (Int) -> Unit
) {
    SettingsCard(
        title = "Maintenance Reminders",
        icon = Icons.Default.Notifications,
        description = "Configure notifications for upcoming maintenance"
    ) {
        MaintenanceSettings(
            settings = settings,
            onMaintenanceRemindersEnabledChange = onMaintenanceRemindersEnabledChange,
            onAdvanceWarningDaysChange = onAdvanceWarningDaysChange
        )
    }
}

@Composable
fun BackupAndRestoreCard(
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    SettingsCard(
        title = "Data Management",
        icon = Icons.Default.Backup,
        description = "Backup your data or restore from a previous backup"
    ) {
        BackupAndRestoreSettings(
            onBackup = onBackup,
            onRestore = onRestore
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            
            content()
        }
    }
}

@Composable
fun MaintenanceSettings(
    settings: UserPreferences,
    onMaintenanceRemindersEnabledChange: (Boolean) -> Unit,
    onAdvanceWarningDaysChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enable Reminders",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Get notified about upcoming maintenance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            Switch(
                checked = settings.maintenanceRemindersEnabled,
                onCheckedChange = onMaintenanceRemindersEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        
        if (settings.maintenanceRemindersEnabled) {
            OutlinedTextField(
                value = settings.advanceWarningDays.toString(),
                onValueChange = { onAdvanceWarningDaysChange(it.toIntOrNull() ?: 7) },
                label = { Text("Advance Warning (Days)") },
                placeholder = { Text("7") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun BackupAndRestoreSettings(onBackup: () -> Unit, onRestore: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onBackup,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Default.Backup,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = "Backup",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        Button(
            onClick = onRestore,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Default.Restore,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = "Restore",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(selectedCurrency: AppCurrency, onCurrencySelected: (AppCurrency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedCurrency.name,
            onValueChange = { },
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            AppCurrency.values().forEach { currency ->
                DropdownMenuItem(
                    text = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(currency.name)
                        }
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UnitSystemSelector(selectedUnitSystem: AppUnitSystem, onUnitSystemSelected: (AppUnitSystem) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            onClick = { onUnitSystemSelected(AppUnitSystem.METRIC) },
            label = { Text("Metric") },
            selected = selectedUnitSystem == AppUnitSystem.METRIC,
            leadingIcon = if (selectedUnitSystem == AppUnitSystem.METRIC) {
                {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.weight(1f)
        )
        
        FilterChip(
            onClick = { onUnitSystemSelected(AppUnitSystem.IMPERIAL) },
            label = { Text("Imperial") },
            selected = selectedUnitSystem == AppUnitSystem.IMPERIAL,
            leadingIcon = if (selectedUnitSystem == AppUnitSystem.IMPERIAL) {
                {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
