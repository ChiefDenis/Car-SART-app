package com.chiefdenis.carsart.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

enum class ServiceType {
    MAINTENANCE, REPAIR, INSPECTION, UPGRADE, OTHER
}

@Entity(
    tableName = "service_records",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceRecord(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val vehicleId: UUID,
    val date: Long,
    val mileage: Int,
    val serviceType: ServiceType,
    val provider: String? = null,
    val cost: BigDecimal,
    val notes: String? = null,
    val receiptPhotoUris: List<String> = emptyList(),
    val nextServiceDueDate: Long? = null,
    val nextServiceDueMileage: Int? = null,
    val isSensitive: Boolean = false,
    val linkedMaintenanceTaskId: UUID? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
