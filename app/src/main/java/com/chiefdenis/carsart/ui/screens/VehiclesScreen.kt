package com.chiefdenis.carsart.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.domain.model.Vehicle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    onAddVehicle: () -> Unit,
    onVehicleClick: (UUID) -> Unit,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Vehicles") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddVehicle) {
                Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (vehicles.isEmpty()) {
            EmptyVehiclesState(onAddVehicle)
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(vehicles) { vehicle ->
                    VehicleItem(vehicle = vehicle, onClick = { onVehicleClick(vehicle.id) })
                }
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = vehicle.nickname, style = MaterialTheme.typography.headlineSmall)
            Text(text = "${vehicle.year} ${vehicle.make} ${vehicle.model}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${vehicle.currentMileage} km", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EmptyVehiclesState(onAddVehicle: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No vehicles yet. Tap the + button to add your first car!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
