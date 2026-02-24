package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.VehicleRepository
import java.util.UUID
import javax.inject.Inject

class UpdateMileage @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: UUID, newMileage: Int): Result<Unit> {
        if (newMileage < 0) {
            return Result.failure(IllegalArgumentException("Mileage cannot be negative."))
        }

        vehicleRepository.updateMileage(id, newMileage)
        return Result.success(Unit)
    }
}
