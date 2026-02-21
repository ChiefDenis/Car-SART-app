package com.chiefdenis.carsart.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

enum class MaintenancePriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Entity(
    tableName = "maintenance_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaintenanceTask(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val vehicleId: UUID,
    val taskName: String,
    val description: String,
    val intervalMonths: Int,
    val intervalMileageKm: Int?,
    val lastCheckedDate: Long?,
    val lastCheckedMileage: Int?,
    val nextDueDate: Long?,
    val nextDueMileage: Int?,
    val isActive: Boolean = true,
    val priority: MaintenancePriority,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
