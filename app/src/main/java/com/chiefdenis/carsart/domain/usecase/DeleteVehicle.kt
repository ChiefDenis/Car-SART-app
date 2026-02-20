package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.VehicleRepository
import java.util.UUID
import javax.inject.Inject

class DeleteVehicle @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: UUID): Result<Unit> {
        if (vehicleRepository.getVehicleById(id) == null) {
            return Result.failure(IllegalArgumentException("Vehicle not found."))
        }
        vehicleRepository.deleteVehicleById(id)
        return Result.success(Unit)
    }
}
