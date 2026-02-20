package com.chiefdenis.carsart.domain.model

import com.chiefdenis.carsart.data.database.VehicleType
import java.util.UUID

data class Vehicle(
    val id: UUID,
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String?,
    val licensePlate: String?,
    val currentMileage: Int,
    val photoUri: String?,
    val vehicleType: VehicleType
)
