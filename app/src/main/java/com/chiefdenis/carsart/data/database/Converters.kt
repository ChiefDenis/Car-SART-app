package com.chiefdenis.carsart.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): Double? {
        return value?.toDouble()
    }

    @TypeConverter
    fun toBigDecimal(value: Double?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromUUID(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun toUUID(value: String?): UUID? = value?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromPriority(priority: MaintenancePriority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): MaintenancePriority = MaintenancePriority.valueOf(value)

    @TypeConverter
    fun fromVehicleType(vehicleType: VehicleType): String = vehicleType.name

    @TypeConverter
    fun toVehicleType(value: String): VehicleType = VehicleType.valueOf(value)

    @TypeConverter
    fun fromServiceType(serviceType: ServiceType): String = serviceType.name

    @TypeConverter
    fun toServiceType(value: String): ServiceType = ServiceType.valueOf(value)
}
