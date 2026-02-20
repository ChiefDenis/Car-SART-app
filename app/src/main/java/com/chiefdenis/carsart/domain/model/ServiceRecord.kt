package com.chiefdenis.carsart.domain.model

import com.chiefdenis.carsart.data.database.ServiceType
import java.math.BigDecimal
import java.util.UUID

data class ServiceRecord(
    val id: UUID,
    val vehicleId: UUID,
    val date: Long,
    val mileage: Int,
    val serviceType: ServiceType,
    val provider: String?,
    val cost: BigDecimal,
    val notes: String?,
    val receiptPhotoUris: List<String>,
    val nextServiceDueDate: Long?,
    val nextServiceDueMileage: Int?
)
