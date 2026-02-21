package com.chiefdenis.carsart.data.repository

import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.MaintenanceTaskDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface MaintenanceRepository {
    suspend fun addMaintenanceTask(task: MaintenanceTask)
    suspend fun updateMaintenanceTask(task: MaintenanceTask)
    fun getTasksForVehicle(vehicleId: UUID): Flow<List<MaintenanceTask>>
    fun getActiveTasks(): Flow<List<MaintenanceTask>>
    suspend fun deleteTask(id: UUID)
}

@Singleton
class MaintenanceRepositoryImpl @Inject constructor(
    private val maintenanceTaskDao: MaintenanceTaskDao
) : MaintenanceRepository {

    override suspend fun addMaintenanceTask(task: MaintenanceTask) {
        maintenanceTaskDao.insert(task)
    }

    override suspend fun updateMaintenanceTask(task: MaintenanceTask) {
        maintenanceTaskDao.update(task.copy(updatedAt = System.currentTimeMillis()))
    }

    override fun getTasksForVehicle(vehicleId: UUID): Flow<List<MaintenanceTask>> {
        return maintenanceTaskDao.getTasksForVehicle(vehicleId)
    }

    override fun getActiveTasks(): Flow<List<MaintenanceTask>> {
        return maintenanceTaskDao.getActiveTasks()
    }

    override suspend fun deleteTask(id: UUID) {
        maintenanceTaskDao.deleteTask(id)
    }
}
