package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.database.VehicleType
import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.domain.model.Vehicle as DomainVehicle
import com.chiefdenis.carsart.data.database.Vehicle as DbVehicle
import java.util.UUID
import javax.inject.Inject

class AddVehicle @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(
        nickname: String,
        make: String,
        model: String,
        year: Int,
        vin: String?,
        licensePlate: String?,
        currentMileage: Int,
        photoUri: String?,
        vehicleType: VehicleType
    ): Result<Unit> {
        if (nickname.isBlank()) {
            return Result.failure(IllegalArgumentException("Vehicle nickname cannot be empty."))
        }
        if (make.isBlank()) {
            return Result.failure(IllegalArgumentException("Vehicle make cannot be empty."))
        }
        if (model.isBlank()) {
            return Result.failure(IllegalArgumentException("Vehicle model cannot be empty."))
        }
        if (currentMileage < 0) {
            return Result.failure(IllegalArgumentException("Mileage cannot be negative."))
        }

        val vehicle = DbVehicle(
            id = UUID.randomUUID(),
            nickname = nickname,
            make = make,
            model = model,
            year = year,
            vin = vin,
            licensePlate = licensePlate,
            currentMileage = currentMileage,
            photoUri = photoUri,
            vehicleType = vehicleType
        )
        vehicleRepository.addVehicle(vehicle)
        return Result.success(Unit)
    }
}
