package com.chiefdenis.carsart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class VehicleType {
    SEDAN, SUV, TRUCK, MOTORCYCLE, OTHER
}

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String? = null,
    val licensePlate: String? = null,
    val currentMileage: Int,
    val purchaseDate: Long? = null,
    val photoUri: String? = null,
    val vehicleType: VehicleType = VehicleType.SEDAN,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
