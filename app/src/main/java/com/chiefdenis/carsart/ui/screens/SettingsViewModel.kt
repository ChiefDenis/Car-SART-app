package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import com.chiefdenis.carsart.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val currency: StateFlow<AppCurrency> = userPreferencesRepository.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCurrency.NGN)

    val unitSystem: StateFlow<AppUnitSystem> = userPreferencesRepository.unitSystem
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUnitSystem.METRIC)

    fun setCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            userPreferencesRepository.setCurrency(currency)
        }
    }

    fun setUnitSystem(unitSystem: AppUnitSystem) {
        viewModelScope.launch {
            userPreferencesRepository.setUnitSystem(unitSystem)
        }
    }
}
