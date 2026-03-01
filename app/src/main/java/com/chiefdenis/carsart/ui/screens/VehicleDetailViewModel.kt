package com.chiefdenis.carsart.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.domain.model.Vehicle
import com.chiefdenis.carsart.domain.model.ServiceRecord
import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.domain.usecase.GetServiceHistory
import com.chiefdenis.carsart.domain.usecase.GetVehicle
import com.chiefdenis.carsart.domain.usecase.GetMaintenanceTasks
import com.chiefdenis.carsart.utils.PdfExporter
import com.chiefdenis.carsart.utils.PdfExportConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import com.chiefdenis.carsart.domain.toData

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    private val getVehicle: GetVehicle,
    private val getServiceHistory: GetServiceHistory,
    private val getMaintenanceTasks: GetMaintenanceTasks,
    private val pdfExporter: PdfExporter,
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: UUID = UUID.fromString(savedStateHandle.get<String>("vehicleId")!!)

    val vehicle: StateFlow<com.chiefdenis.carsart.domain.model.Vehicle?> = getVehicle(vehicleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val serviceHistory: StateFlow<List<com.chiefdenis.carsart.domain.model.ServiceRecord>> = getServiceHistory(vehicleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val maintenanceTasks: StateFlow<List<MaintenanceTask>> = flow { emit(getMaintenanceTasks(vehicleId)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun exportToPdf() {
        viewModelScope.launch {
            try {
                val currentVehicle = vehicle.value
                val currentServiceHistory = serviceHistory.value
                val currentMaintenanceTasks = maintenanceTasks.value

                if (currentVehicle != null) {
                    val config = PdfExportConfig(
                        currency = "NGN",
                        units = "METRIC",
                        includeImages = true,
                        includeMaintenanceSection = true
                    )

                    val uri = pdfExporter.exportServiceHistory(
                        vehicle = currentVehicle.toData(),
                        serviceRecords = currentServiceHistory.map { it.toData() },
                        maintenanceTasks = currentMaintenanceTasks,
                        config = config
                    )

                    if (uri != null) {
                        Log.d("PdfExporter", "PDF exported successfully to $uri")
                    } else {
                        Log.e("PdfExporter", "PDF export failed")
                    }
                }
            } catch (e: Exception) {
                Log.e("PdfExporter", "PDF export error", e)
            }
        }
    }
}
