package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import com.chiefdenis.carsart.domain.model.ServiceRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import com.chiefdenis.carsart.data.database.ServiceRecord as DbServiceRecord

class GetServiceHistory @Inject constructor(
    private val serviceRecordRepository: ServiceRecordRepository
) {
    operator fun invoke(vehicleId: UUID): Flow<List<ServiceRecord>> {
        return serviceRecordRepository.getServiceRecordsForVehicle(vehicleId).map {
            it.map { dbServiceRecord -> dbServiceRecord.toDomainModel() }
        }
    }
}

fun DbServiceRecord.toDomainModel(): ServiceRecord {
    return ServiceRecord(
        id = id,
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
}
