package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.database.VehicleType
import com.chiefdenis.carsart.domain.usecase.AddVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddVehicleViewModel @Inject constructor(
    private val addVehicle: AddVehicle
) : ViewModel() {

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _make = MutableStateFlow("")
    val make = _make.asStateFlow()

    private val _model = MutableStateFlow("")
    val model = _model.asStateFlow()

    private val _year = MutableStateFlow("")
    val year = _year.asStateFlow()

    private val _mileage = MutableStateFlow("")
    val mileage = _mileage.asStateFlow()

    fun onNicknameChange(nickname: String) {
        _nickname.value = nickname
    }

    fun onMakeChange(make: String) {
        _make.value = make
    }

    fun onModelChange(model: String) {
        _model.value = model
    }

    fun onYearChange(year: String) {
        _year.value = year
    }

    fun onMileageChange(mileage: String) {
        _mileage.value = mileage
    }

    fun saveVehicle() {
        viewModelScope.launch {
            addVehicle(
                nickname = _nickname.value,
                make = _make.value,
                model = _model.value,
                year = _year.value.toInt(),
                vin = null,
                licensePlate = null,
                currentMileage = _mileage.value.toInt(),
                photoUri = null,
                vehicleType = VehicleType.SEDAN
            )
        }
    }
}
