package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.chiefdenis.carsart.data.database.Vehicle as DbVehicle

class GetVehicles @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> {
        return vehicleRepository.getAllVehicles().map {
            it.map { dbVehicle -> dbVehicle.toDomainModel() }
        }
    }
}

fun DbVehicle.toDomainModel(): Vehicle {
    return Vehicle(
        id = id,
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
}
