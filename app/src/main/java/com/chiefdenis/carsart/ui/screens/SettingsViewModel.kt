package com.chiefdenis.carsart.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.ServiceRecord
import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.repository.AppCurrency
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import com.chiefdenis.carsart.data.repository.ThemeMode
import com.chiefdenis.carsart.data.repository.UserPreferences
import com.chiefdenis.carsart.data.repository.UserPreferencesRepository
import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.utils.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

data class AppBackupData(
    val vehicles: List<Vehicle>,
    val serviceRecords: List<ServiceRecord>,
    val maintenanceTasks: List<MaintenanceTask>
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val backupManager: BackupManager,
    private val vehicleRepository: VehicleRepository,
    private val serviceRecordRepository: ServiceRecordRepository,
    private val maintenanceRepository: MaintenanceRepository
) : ViewModel() {

    val settings: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(AppCurrency.NGN, AppUnitSystem.METRIC, ThemeMode.SYSTEM, true, 7)
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

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(themeMode)
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

    fun backup() {
        viewModelScope.launch {
            try {
                Log.d("SettingsViewModel", "Starting backup...")
                
                // Get all data from repositories
                val vehicles = vehicleRepository.getAllVehicles().first()
                val serviceRecords = serviceRecordRepository.getAllServiceRecords().first()
                val maintenanceTasks = maintenanceRepository.getAllMaintenanceTasks().first()
                
                val backupData = AppBackupData(
                    vehicles = vehicles,
                    serviceRecords = serviceRecords,
                    maintenanceTasks = maintenanceTasks
                )
                
                val success = backupManager.backup(backupData)
                Log.d("SettingsViewModel", "Backup ${if (success) "completed successfully" else "failed"}")
                Log.d("SettingsViewModel", "Backed up ${vehicles.count()} vehicles, ${serviceRecords.count()} service records, ${maintenanceTasks.count()} maintenance tasks")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Backup error: ${e.message}", e)
            }
        }
    }

    suspend fun backupToFile(context: Context, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Get all data from repositories
                val vehicles = vehicleRepository.getAllVehicles().first()
                val serviceRecords = serviceRecordRepository.getAllServiceRecords().first()
                val maintenanceTasks = maintenanceRepository.getAllMaintenanceTasks().first()
                
                val backupData = AppBackupData(
                    vehicles = vehicles,
                    serviceRecords = serviceRecords,
                    maintenanceTasks = maintenanceTasks
                )
                
                val jsonData = backupManager.gson.toJson(backupData)
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonData.toByteArray())
                }
                
                // Show success message with counts
                val message = "Backup complete!\n${vehicles.count()} vehicles, ${serviceRecords.count()} service records, ${maintenanceTasks.count()} maintenance tasks"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
                
                true
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }

    suspend fun restoreFromFile(context: Context, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("SettingsViewModel", "Starting restore from file: $uri")
                
                val jsonData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } ?: return@withContext false
                
                val backupData = backupManager.gson.fromJson(jsonData, AppBackupData::class.java)
                
                // Clear existing data first
                backupData.vehicles.forEach { vehicleRepository.deleteVehicleById(it.id) }
                backupData.serviceRecords.forEach { serviceRecordRepository.deleteServiceRecordById(it.id) }
                backupData.maintenanceTasks.forEach { maintenanceRepository.deleteMaintenanceTaskById(it.id) }
                
                // Insert restored data
                backupData.vehicles.forEach { vehicleRepository.addVehicle(it) }
                backupData.serviceRecords.forEach { serviceRecordRepository.addServiceRecord(it) }
                backupData.maintenanceTasks.forEach { maintenanceRepository.addMaintenanceTask(it) }
                
                Log.d("SettingsViewModel", "Restore from file completed successfully")
                Log.d("SettingsViewModel", "Restored ${backupData.vehicles.count()} vehicles, ${backupData.serviceRecords.count()} service records, ${backupData.maintenanceTasks.count()} maintenance tasks")
                true
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Restore from file error: ${e.message}", e)
                false
            }
        }
    }

    suspend fun restore(): Boolean {
        return try {
            Log.d("SettingsViewModel", "Starting restore...")
            
            val backupData = backupManager.restore(AppBackupData::class.java)
            if (backupData != null) {
                // Clear existing data first
                backupData.vehicles.forEach { vehicleRepository.deleteVehicleById(it.id) }
                backupData.serviceRecords.forEach { serviceRecordRepository.deleteServiceRecordById(it.id) }
                backupData.maintenanceTasks.forEach { maintenanceRepository.deleteMaintenanceTaskById(it.id) }
                
                // Insert restored data
                backupData.vehicles.forEach { vehicleRepository.addVehicle(it) }
                backupData.serviceRecords.forEach { serviceRecordRepository.addServiceRecord(it) }
                backupData.maintenanceTasks.forEach { maintenanceRepository.addMaintenanceTask(it) }
                
                Log.d("SettingsViewModel", "Restore completed successfully")
                Log.d("SettingsViewModel", "Restored ${backupData.vehicles.count()} vehicles, ${backupData.serviceRecords.count()} service records, ${backupData.maintenanceTasks.count()} maintenance tasks")
                true
            } else {
                Log.w("SettingsViewModel", "No backup data found")
                false
            }
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Restore error: ${e.message}", e)
            false
        }
    }

    fun hasBackup(): Boolean {
        return backupManager.hasBackup()
    }

    fun getBackupTimestamp(): Long? {
        return backupManager.getBackupTimestamp()
    }
}
