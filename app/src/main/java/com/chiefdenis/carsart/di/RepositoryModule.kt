package com.chiefdenis.carsart.di

import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.data.repository.VehicleRepositoryImpl
import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import com.chiefdenis.carsart.data.repository.ServiceRecordRepositoryImpl
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.MaintenanceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindServiceRecordRepository(impl: ServiceRecordRepositoryImpl): ServiceRecordRepository

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(impl: MaintenanceRepositoryImpl): MaintenanceRepository
}
