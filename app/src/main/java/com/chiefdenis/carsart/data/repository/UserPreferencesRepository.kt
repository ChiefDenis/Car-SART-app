package com.chiefdenis.carsart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppCurrency(val code: String) {
    NGN("NGN"),
    USD("USD"),
    EUR("EUR"),
    GBP("GBP")
}

enum class AppUnitSystem {
    METRIC, IMPERIAL
}

data class UserPreferences(
    val currency: AppCurrency = AppCurrency.NGN,
    val unitSystem: AppUnitSystem = AppUnitSystem.METRIC,
    val maintenanceRemindersEnabled: Boolean = true,
    val advanceWarningDays: Int = 7
)

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val CURRENCY = stringPreferencesKey("currency")
        val UNIT_SYSTEM = stringPreferencesKey("unit_system")
        val MAINTENANCE_REMINDERS_ENABLED = booleanPreferencesKey("maintenance_reminders_enabled")
        val ADVANCE_WARNING_DAYS = intPreferencesKey("advance_warning_days")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val currency = AppCurrency.valueOf(preferences[PreferencesKeys.CURRENCY] ?: AppCurrency.NGN.name)
            val unitSystem = AppUnitSystem.valueOf(preferences[PreferencesKeys.UNIT_SYSTEM] ?: AppUnitSystem.METRIC.name)
            val maintenanceRemindersEnabled = preferences[PreferencesKeys.MAINTENANCE_REMINDERS_ENABLED] ?: true
            val advanceWarningDays = preferences[PreferencesKeys.ADVANCE_WARNING_DAYS] ?: 7
            UserPreferences(currency, unitSystem, maintenanceRemindersEnabled, advanceWarningDays)
        }

    suspend fun setCurrency(currency: AppCurrency): Boolean {
        context.dataStore.edit {
            it[PreferencesKeys.CURRENCY] = currency.name
        }
        return true
    }

    suspend fun setUnitSystem(unitSystem: AppUnitSystem): Boolean {
        context.dataStore.edit {
            it[PreferencesKeys.UNIT_SYSTEM] = unitSystem.name
        }
        return true
    }

    suspend fun setMaintenanceRemindersEnabled(enabled: Boolean): Boolean {
        context.dataStore.edit {
            it[PreferencesKeys.MAINTENANCE_REMINDERS_ENABLED] = enabled
        }
        return true
    }

    suspend fun setAdvanceWarningDays(days: Int): Boolean {
        context.dataStore.edit {
            it[PreferencesKeys.ADVANCE_WARNING_DAYS] = days
        }
        return true
    }
}
