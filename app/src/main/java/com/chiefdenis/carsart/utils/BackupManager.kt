package com.chiefdenis.carsart.utils

import android.content.Context
import com.chiefdenis.carsart.data.database.MaintenanceTaskDao
import com.chiefdenis.carsart.data.database.ServiceRecordDao
import com.chiefdenis.carsart.data.database.VehicleDao
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class BackupData(
    val vehicles: List<com.chiefdenis.carsart.data.database.Vehicle>,
    val serviceRecords: List<com.chiefdenis.carsart.data.database.ServiceRecord>,
    val maintenanceTasks: List<com.chiefdenis.carsart.data.database.MaintenanceTask>
)

class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vehicleDao: VehicleDao,
    private val serviceRecordDao: ServiceRecordDao,
    private val maintenanceTaskDao: MaintenanceTaskDao,
    private val gson: Gson
) {

    private fun getBackupFile(): File {
        return File(context.filesDir, "carsart_backup.json")
    }

    suspend fun backup(): Boolean {
        withContext(Dispatchers.IO) {
            val data = BackupData(
                vehicles = vehicleDao.getAll(),
                serviceRecords = serviceRecordDao.getAll(),
                maintenanceTasks = maintenanceTaskDao.getAll()
            )
            val json = gson.toJson(data)
            getBackupFile().writeText(json)
        }
        return true
    }

    suspend fun restore(): Boolean {
        withContext(Dispatchers.IO) {
            val backupFile = getBackupFile()
            if (backupFile.exists()) {
                val json = backupFile.readText()
                val data = gson.fromJson(json, BackupData::class.java)

                // Clear existing data
                vehicleDao.deleteAll()
                serviceRecordDao.deleteAll()
                maintenanceTaskDao.deleteAll()

                // Insert restored data
                data.vehicles.forEach { vehicleDao.insert(it) }
                data.serviceRecords.forEach { serviceRecordDao.insert(it) }
                data.maintenanceTasks.forEach { maintenanceTaskDao.insert(it) }
            }
        }
        return true
    }
}
