package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.domain.model.Vehicle
import com.chiefdenis.carsart.domain.usecase.GetVehicles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    getVehicles: GetVehicles
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = getVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
