package com.chiefdenis.carsart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppCurrency(val code: String) {
    NGN("NGN"),
    USD("USD"),
    EUR("EUR"),
    GBP("GBP")
}

enum class AppUnitSystem {
    METRIC, IMPERIAL
}

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val CURRENCY = stringPreferencesKey("currency")
        val UNIT_SYSTEM = stringPreferencesKey("unit_system")
    }

    val currency: Flow<AppCurrency> = context.dataStore.data.map {
        AppCurrency.valueOf(it[PreferencesKeys.CURRENCY] ?: AppCurrency.NGN.name)
    }

    val unitSystem: Flow<AppUnitSystem> = context.dataStore.data.map {
        AppUnitSystem.valueOf(it[PreferencesKeys.UNIT_SYSTEM] ?: AppUnitSystem.METRIC.name)
    }

    suspend fun setCurrency(currency: AppCurrency) {
        context.dataStore.edit {
            it[PreferencesKeys.CURRENCY] = currency.name
        }
    }

    suspend fun setUnitSystem(unitSystem: AppUnitSystem) {
        context.dataStore.edit {
            it[PreferencesKeys.UNIT_SYSTEM] = unitSystem.name
        }
    }
}
