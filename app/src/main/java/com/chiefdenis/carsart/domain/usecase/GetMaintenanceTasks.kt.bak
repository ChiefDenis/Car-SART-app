package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.database.MaintenanceTask
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class GetMaintenanceTasks @Inject constructor(
    private val maintenanceRepository: MaintenanceRepository
) {
    suspend operator fun invoke(vehicleId: UUID): List<MaintenanceTask> {
        return try {
            maintenanceRepository.getTasksForVehicle(vehicleId).first()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
