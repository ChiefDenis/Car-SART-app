package com.chiefdenis.carsart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Vehicle::class, ServiceRecord::class, MaintenanceTask::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceRecordDao(): ServiceRecordDao
    abstract fun maintenanceTaskDao(): MaintenanceTaskDao

    companion object {
        const val DATABASE_NAME = "carsart.db"
    }
}
