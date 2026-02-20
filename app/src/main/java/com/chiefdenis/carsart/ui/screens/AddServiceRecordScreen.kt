package com.chiefdenis.carsart.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddServiceRecordScreen(viewModel: AddServiceRecordViewModel = hiltViewModel(), onServiceRecordAdded: () -> Unit) {
    val date by viewModel.date.collectAsState()
    val mileage by viewModel.mileage.collectAsState()
    val cost by viewModel.cost.collectAsState()

    Scaffold {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            OutlinedTextField(value = date, onValueChange = viewModel::onDateChange, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mileage, onValueChange = viewModel::onMileageChange, label = { Text("Mileage") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cost, onValueChange = viewModel::onCostChange, label = { Text("Cost") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                viewModel.saveServiceRecord()
                onServiceRecordAdded()
            }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Save")
            }
        }
    }
}
