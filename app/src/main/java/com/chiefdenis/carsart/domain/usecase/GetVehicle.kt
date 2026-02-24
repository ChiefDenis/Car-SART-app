package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GetVehicle @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(id: UUID): Flow<Vehicle?> {
        return vehicleRepository.getVehicleByIdFlow(id).map { dbVehicle ->
            dbVehicle?.toDomainModel()
        }
    }
}
