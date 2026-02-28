package com.chiefdenis.carsart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle): Int

    @Query("SELECT * FROM vehicles ORDER BY nickname ASC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles")
    fun getAll(): List<Vehicle>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleByIdFlow(id: UUID): Flow<Vehicle?>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleById(id: UUID): Vehicle?

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicleById(id: UUID): Int

    @Query("UPDATE vehicles SET currentMileage = :mileage, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateMileage(id: UUID, mileage: Int, timestamp: Long): Int

    // FTS Search
    @Query("SELECT * FROM vehicles WHERE nickname LIKE '%' || :query || '%' OR make LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' COLLATE NOCASE")
    fun searchVehicles(query: String): Flow<List<Vehicle>>

    @Query("DELETE FROM vehicles")
    suspend fun deleteAll(): Int
}
