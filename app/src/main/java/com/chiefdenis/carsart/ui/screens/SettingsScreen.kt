package com.chiefdenis.carsart.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val currency by viewModel.currency.collectAsState()
    val unitSystem by viewModel.unitSystem.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        CurrencyPicker(selectedCurrency = currency, onCurrencySelected = { viewModel.setCurrency(it) })
        UnitSystemSelector(selectedUnitSystem = unitSystem, onUnitSystemSelected = { viewModel.setUnitSystem(it) })
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
