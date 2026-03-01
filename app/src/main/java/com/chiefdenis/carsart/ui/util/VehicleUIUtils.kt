package com.chiefdenis.carsart.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.vector.ImageVector
import com.chiefdenis.carsart.data.database.VehicleType

fun getVehicleTypeIcon(vehicleType: VehicleType): ImageVector {
    return when (vehicleType) {
        VehicleType.SEDAN -> Icons.Default.DirectionsCar
        VehicleType.SUV -> Icons.Default.DirectionsCar
        VehicleType.TRUCK -> Icons.Default.DirectionsCar
        VehicleType.MOTORCYCLE -> Icons.Default.DirectionsCar
        else -> Icons.Default.DirectionsCar
    }
}

fun getVehicleTypeDisplayName(vehicleType: VehicleType): String {
    return when (vehicleType) {
        VehicleType.SEDAN -> "Sedan"
        VehicleType.SUV -> "SUV"
        VehicleType.TRUCK -> "Truck"
        VehicleType.MOTORCYCLE -> "Motorcycle"
        else -> "Other"
    }
}
