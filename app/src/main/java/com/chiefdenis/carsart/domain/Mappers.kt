package com.chiefdenis.carsart.domain

import com.chiefdenis.carsart.data.database.ServiceRecord as ServiceRecordData
import com.chiefdenis.carsart.domain.model.ServiceRecord as ServiceRecordDomain
import com.chiefdenis.carsart.data.database.Vehicle as VehicleData
import com.chiefdenis.carsart.domain.model.Vehicle as VehicleDomain

fun VehicleDomain.toData(): VehicleData {
    return VehicleData(
        id = this.id,
        nickname = this.nickname,
        make = this.make,
        model = this.model,
        year = this.year,
        vin = this.vin,
        licensePlate = this.licensePlate,
        currentMileage = this.currentMileage,
        photoUri = this.photoUri,
        vehicleType = this.vehicleType
    )
}

fun ServiceRecordDomain.toData(): ServiceRecordData {
    return ServiceRecordData(
        id = this.id,
        vehicleId = this.vehicleId,
        date = this.date,
        mileage = this.mileage,
        serviceType = this.serviceType,
        cost = this.cost,
        provider = this.provider,
        notes = this.notes
    )
}
