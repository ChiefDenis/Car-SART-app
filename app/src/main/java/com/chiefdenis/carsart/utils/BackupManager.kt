package com.chiefdenis.carsart.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class SimpleBackupData(
    val timestamp: Long,
    val version: String,
    val data: String // JSON string of all data
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    val gson: Gson // Make gson public
) {

    private fun getBackupFile(): File {
        return File(context.filesDir, "carsart_backup.csart")
    }

    suspend fun backup(data: Any): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("BackupManager", "Starting backup...")
                
                val jsonData = gson.toJson(data)
                val backupData = SimpleBackupData(
                    timestamp = System.currentTimeMillis(),
                    version = "1.0",
                    data = jsonData
                )
                
                val backupFile = getBackupFile()
                val backupJson = gson.toJson(backupData)
                backupFile.writeText(backupJson)
                
                Log.d("BackupManager", "Backup completed successfully")
                true
            } catch (e: Exception) {
                Log.e("BackupManager", "Backup failed: ${e.message}", e)
                false
            }
        }
    }

    suspend fun <T> restore(clazz: Class<T>): T? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("BackupManager", "Starting restore...")
                
                val backupFile = getBackupFile()
                if (!backupFile.exists()) {
                    Log.w("BackupManager", "No backup file found")
                    return@withContext null
                }
                
                val backupJson = backupFile.readText()
                val backupData = gson.fromJson(backupJson, SimpleBackupData::class.java)
                
                Log.d("BackupManager", "Restore completed successfully")
                gson.fromJson(backupData.data, clazz)
            } catch (e: Exception) {
                Log.e("BackupManager", "Restore failed: ${e.message}", e)
                null
            }
        }
    }

    fun hasBackup(): Boolean {
        return getBackupFile().exists()
    }

    fun getBackupTimestamp(): Long? {
        return try {
            val backupFile = getBackupFile()
            if (backupFile.exists()) {
                val backupJson = backupFile.readText()
                val backupData = gson.fromJson(backupJson, SimpleBackupData::class.java)
                backupData.timestamp
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
