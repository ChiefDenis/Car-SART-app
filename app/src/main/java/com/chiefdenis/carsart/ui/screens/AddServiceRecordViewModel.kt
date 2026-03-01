package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.database.ServiceType
import com.chiefdenis.carsart.domain.usecase.AddServiceRecord
import com.chiefdenis.carsart.domain.usecase.ValidateServiceInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddServiceRecordViewModel @Inject constructor(
    private val addServiceRecord: AddServiceRecord,
    private val validateServiceInput: ValidateServiceInput,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: UUID = UUID.fromString(savedStateHandle.get<String>("vehicleId")!!)

    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()

    private val _mileage = MutableStateFlow("")
    val mileage = _mileage.asStateFlow()

    private val _cost = MutableStateFlow("")
    val cost = _cost.asStateFlow()

    private val _serviceType = MutableStateFlow(ServiceType.MAINTENANCE)
    val serviceType = _serviceType.asStateFlow()

    private val _provider = MutableStateFlow("")
    val provider = _provider.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    private val _receiptPhotos = MutableStateFlow<List<String>>(emptyList())
    val receiptPhotos = _receiptPhotos.asStateFlow()

    private val _nextServiceDueDate = MutableStateFlow<String?>(null)
    val nextServiceDueDate = _nextServiceDueDate.asStateFlow()

    private val _nextServiceDueMileage = MutableStateFlow<String?>(null)
    val nextServiceDueMileage = _nextServiceDueMileage.asStateFlow()

    fun onDateChange(date: String) {
        _date.value = date
    }

    fun onMileageChange(mileage: String) {
        _mileage.value = mileage
    }

    fun onCostChange(cost: String) {
        _cost.value = cost
    }

    fun onServiceTypeChange(serviceType: ServiceType) {
        _serviceType.value = serviceType
    }

    fun onProviderChange(provider: String) {
        _provider.value = provider
    }

    fun onNotesChange(notes: String) {
        _notes.value = notes
    }

    fun onAddPhoto(photo: String) {
        _receiptPhotos.value = _receiptPhotos.value + photo
    }

    fun onRemovePhoto(photo: String) {
        _receiptPhotos.value = _receiptPhotos.value - photo
    }

    fun onNextServiceDueDateChange(date: String) {
        _nextServiceDueDate.value = date
    }

    fun onNextServiceDueMileageChange(mileage: String) {
        _nextServiceDueMileage.value = mileage
    }

    fun saveServiceRecord() {
        val mileageResult = validateServiceInput.validateMileage(_mileage.value)
        val costResult = validateServiceInput.validateCost(_cost.value)

        if (!mileageResult.successful || !costResult.successful) {
            // TODO: Show error to user
            return
        }

        viewModelScope.launch {
            addServiceRecord(
                vehicleId = vehicleId,
                date = if (_date.value.isNotEmpty()) {
                    try {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(_date.value)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                } else {
                    System.currentTimeMillis()
                },
                mileage = _mileage.value.toIntOrNull() ?: 0,
                serviceType = _serviceType.value,
                provider = _provider.value.takeIf { it.isNotEmpty() },
                cost = _cost.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                notes = _notes.value.takeIf { it.isNotEmpty() },
                receiptPhotoUris = _receiptPhotos.value,
                nextServiceDueDate = if (_nextServiceDueDate.value?.isNotEmpty() == true) {
                    try {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(_nextServiceDueDate.value!!)?.time ?: null
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                },
                nextServiceDueMileage = _nextServiceDueMileage.value?.toIntOrNull()
            )
        }
    }
}
