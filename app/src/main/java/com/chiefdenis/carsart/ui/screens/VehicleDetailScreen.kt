package com.chiefdenis.carsart.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.domain.model.ServiceRecord
import com.chiefdenis.carsart.utils.PdfExporter
import java.util.UUID

@Composable
fun VehicleDetailScreen(viewModel: VehicleDetailViewModel = hiltViewModel(), onAddServiceRecord: (UUID) -> Unit) {
    val vehicle by viewModel.vehicle.collectAsState()
    val serviceHistory by viewModel.serviceHistory.collectAsState()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            vehicle?.let {
                FloatingActionButton(onClick = { onAddServiceRecord(it.id) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Service Record")
                }
            }
        }
    ) { padding ->
        vehicle?.let { vehicle ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(text = vehicle.nickname, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                Text(text = "${vehicle.year} ${vehicle.make} ${vehicle.model}")
                Text(text = "${vehicle.currentMileage} km")

                Button(
                    onClick = { 
                        val exporter = PdfExporter(context)
                        val file = exporter.exportServiceHistory(vehicle)
                        if (file != null) {
                            Log.d("PdfExporter", "PDF exported successfully to ${file.absolutePath}")
                        } else {
                            Log.e("PdfExporter", "PDF export failed")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Export to PDF")
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                    items(serviceHistory) { record ->
                        ServiceRecordListItem(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceRecordListItem(record: ServiceRecord) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = record.serviceType.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${record.date}")
            Text(text = "Mileage: ${record.mileage} km")
            Text(text = "Cost: ₦${record.cost}")
        }
    }
}
