package com.chiefdenis.carsart.utils

import com.chiefdenis.carsart.data.repository.AppUnitSystem

object UnitConverter {
    fun convertKmToMiles(km: Int): Double {
        return km * 0.621371
    }

    fun convertMilesToKm(miles: Int): Double {
        return miles / 0.621371
    }

    fun formatDistance(km: Int, unitSystem: AppUnitSystem): String {
        return when (unitSystem) {
            AppUnitSystem.METRIC -> "$km km"
            AppUnitSystem.IMPERIAL -> "%.1f mi".format(convertKmToMiles(km))
        }
    }
}
