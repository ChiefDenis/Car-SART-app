package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.VehicleRepository
import java.util.UUID
import javax.inject.Inject

class UpdateMileage @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: UUID, newMileage: Int): Result<Unit> {
        // This is not ideal, we should be working with flows here.
        // We will fix this later.
        // val vehicle = vehicleRepository.getVehicleById(id).first()
        //     ?: return Result.failure(IllegalArgumentException("Vehicle not found."))

        // if (newMileage < vehicle.currentMileage) {
        //     return Result.failure(IllegalArgumentException("New mileage cannot be less than current mileage."))
        // }

        if (newMileage < 0) {
            return Result.failure(IllegalArgumentException("Mileage cannot be negative."))
        }

        vehicleRepository.updateMileage(id, newMileage)
        return Result.success(Unit)
    }
}
