package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import com.chiefdenis.carsart.data.repository.UserPreferences
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

    val settings: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(AppCurrency.NGN, AppUnitSystem.METRIC, true, 7)
        )

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

    fun setMaintenanceRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setMaintenanceRemindersEnabled(enabled)
        }
    }

    fun setAdvanceWarningDays(days: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setAdvanceWarningDays(days)
        }
    }
}
