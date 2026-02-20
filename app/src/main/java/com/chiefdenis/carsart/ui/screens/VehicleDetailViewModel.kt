package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.domain.model.ServiceRecord
import com.chiefdenis.carsart.domain.model.Vehicle
import com.chiefdenis.carsart.domain.usecase.GetServiceHistory
import com.chiefdenis.carsart.domain.usecase.GetVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    private val getVehicle: GetVehicle,
    private val getServiceHistory: GetServiceHistory,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: UUID = UUID.fromString(savedStateHandle.get<String>("vehicleId")!!)

    val vehicle: StateFlow<Vehicle?> = getVehicle(vehicleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val serviceHistory: StateFlow<List<ServiceRecord>> = getServiceHistory(vehicleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
