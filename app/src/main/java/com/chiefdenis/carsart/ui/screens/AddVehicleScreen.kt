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
fun AddVehicleScreen(viewModel: AddVehicleViewModel = hiltViewModel(), onVehicleAdded: () -> Unit) {
    val nickname by viewModel.nickname.collectAsState()
    val make by viewModel.make.collectAsState()
    val model by viewModel.model.collectAsState()
    val year by viewModel.year.collectAsState()
    val mileage by viewModel.mileage.collectAsState()

    Scaffold {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            OutlinedTextField(value = nickname, onValueChange = viewModel::onNicknameChange, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = make, onValueChange = viewModel::onMakeChange, label = { Text("Make") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = model, onValueChange = viewModel::onModelChange, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = year, onValueChange = viewModel::onYearChange, label = { Text("Year") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mileage, onValueChange = viewModel::onMileageChange, label = { Text("Mileage") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                viewModel.saveVehicle()
                onVehicleAdded()
            }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Save")
            }
        }
    }
}
