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

    fun onDateChange(date: String) {
        _date.value = date
    }

    fun onMileageChange(mileage: String) {
        _mileage.value = mileage
    }

    fun onCostChange(cost: String) {
        _cost.value = cost
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
                date = System.currentTimeMillis(), // TODO: Use date picker
                mileage = _mileage.value.toInt(),
                serviceType = ServiceType.MAINTENANCE, // TODO: Use selector
                provider = null,
                cost = _cost.value.toBigDecimal(),
                notes = null,
                receiptPhotoUris = emptyList(),
                nextServiceDueDate = null,
                nextServiceDueMileage = null
            )
        }
    }
}
