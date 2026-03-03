package com.chiefdenis.carsart.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.chiefdenis.carsart.data.database.ServiceType

object ServiceTypeUtils {
    fun getIcon(serviceType: ServiceType): ImageVector {
        return when (serviceType) {
            ServiceType.MAINTENANCE -> Icons.Default.Edit
            ServiceType.REPAIR -> Icons.Default.Build
            ServiceType.INSPECTION -> Icons.Default.Search
            ServiceType.UPGRADE -> Icons.Default.Add
            ServiceType.OTHER -> Icons.Default.MoreHoriz
        }
    }

    fun getDisplayName(serviceType: ServiceType): String {
        return when (serviceType) {
            ServiceType.MAINTENANCE -> "Maintenance"
            ServiceType.REPAIR -> "Repair"
            ServiceType.INSPECTION -> "Inspection"
            ServiceType.UPGRADE -> "Upgrade"
            ServiceType.OTHER -> "Other"
        }
    }
}
