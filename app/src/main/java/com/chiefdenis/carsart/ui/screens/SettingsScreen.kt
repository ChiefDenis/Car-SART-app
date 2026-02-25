package com.chiefdenis.carsart.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import com.chiefdenis.carsart.data.repository.UserPreferences

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        CurrencyPicker(selectedCurrency = settings.currency, onCurrencySelected = { viewModel.setCurrency(it) })
        UnitSystemSelector(selectedUnitSystem = settings.unitSystem, onUnitSystemSelected = { viewModel.setUnitSystem(it) })
        MaintenanceSettings(
            settings = settings,
            onMaintenanceRemindersEnabledChange = { viewModel.setMaintenanceRemindersEnabled(it) },
            onAdvanceWarningDaysChange = { viewModel.setAdvanceWarningDays(it) }
        )
        BackupAndRestoreSettings(onBackup = { viewModel.backup() }, onRestore = { viewModel.restore() })
    }
}

@Composable
fun MaintenanceSettings(
    settings: UserPreferences,
    onMaintenanceRemindersEnabledChange: (Boolean) -> Unit,
    onAdvanceWarningDaysChange: (Int) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = settings.maintenanceRemindersEnabled,
                onCheckedChange = onMaintenanceRemindersEnabledChange
            )
            Text("Enable Maintenance Reminders")
        }
        TextField(
            value = settings.advanceWarningDays.toString(),
            onValueChange = { onAdvanceWarningDaysChange(it.toIntOrNull() ?: 7) },
            label = { Text("Advance Warning (Days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun BackupAndRestoreSettings(onBackup: () -> Unit, onRestore: () -> Unit) {
    Row {
        Button(onClick = onBackup) {
            Text("Backup Data")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onRestore) {
            Text("Restore Data")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(selectedCurrency: AppCurrency, onCurrencySelected: (AppCurrency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selectedCurrency.name,
            onValueChange = { },
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppCurrency.values().forEach { currency ->
                DropdownMenuItem(text = { Text(currency.name) }, onClick = {
                    onCurrencySelected(currency)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun UnitSystemSelector(selectedUnitSystem: AppUnitSystem, onUnitSystemSelected: (AppUnitSystem) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Unit System:")
        Spacer(modifier = Modifier.width(16.dp))
        TextButton(onClick = { onUnitSystemSelected(AppUnitSystem.METRIC) }, enabled = selectedUnitSystem != AppUnitSystem.METRIC) {
            Text("Metric")
        }
        TextButton(onClick = { onUnitSystemSelected(AppUnitSystem.IMPERIAL) }, enabled = selectedUnitSystem != AppUnitSystem.IMPERIAL) {
            Text("Imperial")
        }
    }
}
