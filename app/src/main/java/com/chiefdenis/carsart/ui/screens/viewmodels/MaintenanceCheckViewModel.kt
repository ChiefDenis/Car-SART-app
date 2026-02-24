package com.chiefdenis.carsart.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class MaintenanceCheckUiState(
    val notes: String = "",
    val mileage: String = ""
)

@HiltViewModel
class MaintenanceCheckViewModel @Inject constructor(
    private val maintenanceRepository: MaintenanceRepository,
    private val serviceRecordRepository: ServiceRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceCheckUiState())
    val uiState = _uiState.asStateFlow()

    fun onNotesChange(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun onMileageChange(mileage: String) {
        _uiState.value = _uiState.value.copy(mileage = mileage)
    }

    fun saveLog(taskId: String) {
        viewModelScope.launch {
            val mileage = _uiState.value.mileage.toIntOrNull() ?: 0
            maintenanceRepository.markTaskAsDone(UUID.fromString(taskId), mileage)
            // Create a new ServiceRecord as well
        }
    }
}
