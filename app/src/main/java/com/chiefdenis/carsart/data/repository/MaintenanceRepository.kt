package com.chiefdenis.carsart.data.repository

import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.MaintenanceTaskDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface MaintenanceRepository {
    suspend fun addMaintenanceTask(task: MaintenanceTask): Long
    suspend fun updateMaintenanceTask(task: MaintenanceTask): Int
    fun getTasksForVehicle(vehicleId: UUID): Flow<List<MaintenanceTask>>
    fun getActiveTasks(): Flow<List<MaintenanceTask>>
    suspend fun deleteTask(id: UUID): Int
    suspend fun markTaskAsDone(taskId: UUID, currentMileage: Int): Boolean
}

@Singleton
class MaintenanceRepositoryImpl @Inject constructor(
    private val maintenanceTaskDao: MaintenanceTaskDao
) : MaintenanceRepository {

    override suspend fun addMaintenanceTask(task: MaintenanceTask): Long {
        return maintenanceTaskDao.insert(task)
    }

    override suspend fun updateMaintenanceTask(task: MaintenanceTask): Int {
        return maintenanceTaskDao.update(task.copy(updatedAt = System.currentTimeMillis()))
    }

    override fun getTasksForVehicle(vehicleId: UUID): Flow<List<MaintenanceTask>> {
        return maintenanceTaskDao.getTasksForVehicle(vehicleId)
    }

    override fun getActiveTasks(): Flow<List<MaintenanceTask>> {
        return maintenanceTaskDao.getActiveTasks()
    }

    override suspend fun deleteTask(id: UUID): Int {
        return maintenanceTaskDao.deleteTask(id)
    }

    override suspend fun markTaskAsDone(taskId: UUID, currentMileage: Int): Boolean {
        val task = maintenanceTaskDao.getTaskById(taskId)
        if (task != null) {
            val updatedTask = task.copy(
                lastCheckedDate = System.currentTimeMillis(),
                lastCheckedMileage = currentMileage,
                updatedAt = System.currentTimeMillis()
            )
            maintenanceTaskDao.update(updatedTask)
            return true
        }
        return false
    }
}
