package com.chiefdenis.carsart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MaintenanceTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: MaintenanceTask): Long

    @Update
    suspend fun update(task: MaintenanceTask): Int

    @Query("SELECT * FROM maintenance_tasks WHERE vehicleId = :vehicleId ORDER BY nextDueDate ASC")
    fun getTasksForVehicle(vehicleId: UUID): Flow<List<MaintenanceTask>>

    @Query("SELECT * FROM maintenance_tasks WHERE isActive = 1")
    fun getActiveTasks(): Flow<List<MaintenanceTask>>

    @Query("DELETE FROM maintenance_tasks WHERE id = :id")
    suspend fun deleteTask(id: UUID): Int
}
