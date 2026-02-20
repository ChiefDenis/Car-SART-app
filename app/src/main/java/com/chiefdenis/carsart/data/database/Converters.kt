package com.chiefdenis.carsart.data.database

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        return string?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromVehicleType(type: VehicleType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toVehicleType(name: String?): VehicleType? {
        return name?.let { VehicleType.valueOf(it) }
    }

    @TypeConverter
    fun fromServiceType(type: ServiceType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toServiceType(name: String?): ServiceType? {
        return name?.let { ServiceType.valueOf(it) }
    }
}
