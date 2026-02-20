package com.chiefdenis.carsart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ServiceRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serviceRecord: ServiceRecord): Long

    @Update
    suspend fun update(serviceRecord: ServiceRecord): Int

    @Query("SELECT * FROM service_records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getServiceRecordsForVehicle(vehicleId: UUID): Flow<List<ServiceRecord>>

    @Query("SELECT * FROM service_records WHERE id = :id")
    suspend fun getServiceRecordById(id: UUID): ServiceRecord?

    @Query("DELETE FROM service_records WHERE id = :id")
    suspend fun deleteServiceRecordById(id: UUID): Int
}
