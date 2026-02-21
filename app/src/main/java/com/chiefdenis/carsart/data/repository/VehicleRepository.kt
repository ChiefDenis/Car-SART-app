package com.chiefdenis.carsart.data.repository

import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.database.VehicleDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface VehicleRepository {
    suspend fun addVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    fun getAllVehicles(): Flow<List<Vehicle>>
    fun getVehicleByIdFlow(id: UUID): Flow<Vehicle?>
    fun getVehicleById(id: UUID): Vehicle?
    suspend fun deleteVehicleById(id: UUID)
    suspend fun updateMileage(id: UUID, mileage: Int)
    fun searchVehicles(query: String): Flow<List<Vehicle>>
}

@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    override suspend fun addVehicle(vehicle: Vehicle) {
        vehicleDao.insert(vehicle)
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.update(vehicle.copy(updatedAt = System.currentTimeMillis()))
    }

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getAllVehicles()
    }

    override fun getVehicleByIdFlow(id: UUID): Flow<Vehicle?> {
        return vehicleDao.getVehicleByIdFlow(id)
    }

    override fun getVehicleById(id: UUID): Vehicle? {
        return vehicleDao.getVehicleById(id)
    }

    override suspend fun deleteVehicleById(id: UUID) {
        vehicleDao.deleteVehicleById(id)
    }

    override suspend fun updateMileage(id: UUID, mileage: Int) {
        vehicleDao.updateMileage(id, mileage, System.currentTimeMillis())
    }

    override fun searchVehicles(query: String): Flow<List<Vehicle>> {
        return vehicleDao.searchVehicles(query)
    }
}
