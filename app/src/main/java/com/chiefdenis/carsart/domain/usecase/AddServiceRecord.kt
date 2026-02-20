package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.database.ServiceType
import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import com.chiefdenis.carsart.data.database.ServiceRecord as DbServiceRecord
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class AddServiceRecord @Inject constructor(
    private val serviceRecordRepository: ServiceRecordRepository
) {
    suspend operator fun invoke(
        vehicleId: UUID,
        date: Long,
        mileage: Int,
        serviceType: ServiceType,
        provider: String?,
        cost: BigDecimal,
        notes: String?,
        receiptPhotoUris: List<String>,
        nextServiceDueDate: Long?,
        nextServiceDueMileage: Int?
    ): Result<Unit> {
        if (mileage < 0) {
            return Result.failure(IllegalArgumentException("Mileage cannot be negative."))
        }
        if (cost < BigDecimal.ZERO) {
            return Result.failure(IllegalArgumentException("Cost cannot be negative."))
        }

        val serviceRecord = DbServiceRecord(
            vehicleId = vehicleId,
            date = date,
            mileage = mileage,
            serviceType = serviceType,
            provider = provider,
            cost = cost,
            notes = notes,
            receiptPhotoUris = receiptPhotoUris,
            nextServiceDueDate = nextServiceDueDate,
            nextServiceDueMileage = nextServiceDueMileage
        )
        serviceRecordRepository.addServiceRecord(serviceRecord)
        return Result.success(Unit)
    }
}
