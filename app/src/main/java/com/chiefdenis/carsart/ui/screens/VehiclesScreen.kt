package com.chiefdenis.carsart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.domain.model.Vehicle
import com.chiefdenis.carsart.data.database.VehicleType
import java.util.UUID

@Composable
fun VehiclesScreen(viewModel: VehiclesViewModel = hiltViewModel(), onAddVehicle: () -> Unit) {
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddVehicle() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
            }
        }
    ) { padding ->
        if (vehicles.isEmpty()) {
            EmptyVehiclesScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(vehicles) { vehicle ->
                    VehicleListItem(vehicle = vehicle)
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(vehicle: Vehicle) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = vehicle.nickname, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text(text = "${vehicle.year} ${vehicle.make} ${vehicle.model}")
            Text(text = "${vehicle.currentMileage} km")
        }
    }
}

@Composable
fun EmptyVehiclesScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No vehicles yet. Tap the + button to add one!")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVehicleListItem() {
    val vehicle = Vehicle(
        id = UUID.randomUUID(),
        nickname = "My Awesome Car",
        make = "Toyota",
        model = "Corolla",
        year = 2022,
        vin = "ABC123XYZ",
        licensePlate = "COOL-CAR",
        currentMileage = 15000,
        photoUri = null,
        vehicleType = VehicleType.SEDAN
    )
    VehicleListItem(vehicle = vehicle)
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyVehiclesScreen() {
    EmptyVehiclesScreen()
}
