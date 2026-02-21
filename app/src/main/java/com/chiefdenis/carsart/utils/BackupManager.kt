package com.chiefdenis.carsart.utils

import android.content.Context
import com.chiefdenis.carsart.data.database.AppDatabase
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase
) {

    suspend fun createBackup(password: String): File? {
        val vehicles = appDatabase.vehicleDao().getAll()
        val serviceRecords = appDatabase.serviceRecordDao().getAll()

        val backupData = BackupData(vehicles, serviceRecords)
        val json = Gson().toJson(backupData)

        val backupFile = File(context.filesDir, "Car SART-${System.currentTimeMillis()}.bak")

        return try {
            val key = SecretKeySpec(password.toByteArray().copyOf(32), "AES")
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)

            val encryptedData = cipher.doFinal(json.toByteArray())

            FileOutputStream(backupFile).use {
                it.write(iv)
                it.write(encryptedData)
            }
            backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class BackupData(
    val vehicles: List<com.chiefdenis.carsart.data.database.Vehicle>,
    val serviceRecords: List<com.chiefdenis.carsart.data.database.ServiceRecord>
)
