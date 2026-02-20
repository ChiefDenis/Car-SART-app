package com.chiefdenis.carsart.data.repository

import com.chiefdenis.carsart.data.database.ServiceRecord
import com.chiefdenis.carsart.data.database.ServiceRecordDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface ServiceRecordRepository {
    suspend fun addServiceRecord(serviceRecord: ServiceRecord)
    suspend fun updateServiceRecord(serviceRecord: ServiceRecord)
    fun getServiceRecordsForVehicle(vehicleId: UUID): Flow<List<ServiceRecord>>
    suspend fun getServiceRecordById(id: UUID): ServiceRecord?
    suspend fun deleteServiceRecordById(id: UUID)
}

@Singleton
class ServiceRecordRepositoryImpl @Inject constructor(
    private val serviceRecordDao: ServiceRecordDao
) : ServiceRecordRepository {

    override suspend fun addServiceRecord(serviceRecord: ServiceRecord) {
        serviceRecordDao.insert(serviceRecord)
    }

    override suspend fun updateServiceRecord(serviceRecord: ServiceRecord) {
        serviceRecordDao.update(serviceRecord.copy(updatedAt = System.currentTimeMillis()))
    }

    override fun getServiceRecordsForVehicle(vehicleId: UUID): Flow<List<ServiceRecord>> {
        return serviceRecordDao.getServiceRecordsForVehicle(vehicleId)
    }

    override suspend fun getServiceRecordById(id: UUID): ServiceRecord? {
        return serviceRecordDao.getServiceRecordById(id)
    }

    override suspend fun deleteServiceRecordById(id: UUID) {
        serviceRecordDao.deleteServiceRecordById(id)
    }
}
