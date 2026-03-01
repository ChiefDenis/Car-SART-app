package com.chiefdenis.carsart.domain

import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.database.VehicleType
import com.chiefdenis.carsart.data.database.ServiceRecord
import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.MaintenancePriority
import com.chiefdenis.carsart.data.database.ServiceType
import java.util.UUID
import kotlin.math.roundToInt

data class MaintenanceSuggestion(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: UUID,
    val title: String,
    val description: String,
    val priority: MaintenancePriority,
    val suggestionType: SuggestionType,
    val actionable: Boolean = true,
    val estimatedCost: Double? = null,
    val dueMileage: Int? = null,
    val dueDate: Long? = null
)

enum class SuggestionType {
    SEASONAL,
    MILEAGE_BASED,
    PATTERN_ANALYSIS,
    WEATHER_BASED,
    URGENT,
    PREVENTIVE
}

class SuggestionEngine {
    
    fun generateSuggestions(
        vehicle: Vehicle,
        serviceHistory: List<ServiceRecord>,
        maintenanceTasks: List<MaintenanceTask>,
        currentMileage: Int = vehicle.currentMileage
    ): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        
        // Seasonal suggestions
        suggestions.addAll(generateSeasonalSuggestions(vehicle))
        
        // Mileage-based suggestions
        suggestions.addAll(generateMileageBasedSuggestions(vehicle, currentMileage))
        
        // Pattern analysis suggestions
        suggestions.addAll(generatePatternAnalysisSuggestions(vehicle, serviceHistory))
        
        // Weather-based suggestions (placeholder for future weather API integration)
        suggestions.addAll(generateWeatherBasedSuggestions(vehicle))
        
        // Urgent maintenance suggestions
        suggestions.addAll(generateUrgentSuggestions(vehicle, maintenanceTasks, currentMileage))
        
        return suggestions.sortedByDescending { it.priority }
    }
    
    private fun generateSeasonalSuggestions(vehicle: Vehicle): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        val currentMonth = java.time.LocalDate.now().monthValue
        
        when (currentMonth) {
            // End of year - battery check before harmattan/dry season
            in 10..12 -> {
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "Pre-Harmattan Battery Check",
                        description = "The dry season is approaching. Check your battery health and terminals to prevent starting issues.",
                        priority = MaintenancePriority.MEDIUM,
                        suggestionType = SuggestionType.SEASONAL,
                        estimatedCost = 5000.0
                    )
                )
                
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "Wiper Blade Inspection",
                        description = "Rainy season preparation: Check wiper blades and replace if worn for better visibility.",
                        priority = MaintenancePriority.MEDIUM,
                        suggestionType = SuggestionType.SEASONAL,
                        estimatedCost = 3500.0
                    )
                )
            }
            
            // Rainy season preparation
            in 3..5 -> {
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "Tire Tread Check",
                        description = "Rainy season approaching: Ensure adequate tire tread depth for wet road safety.",
                        priority = MaintenancePriority.HIGH,
                        suggestionType = SuggestionType.SEASONAL,
                        estimatedCost = 0.0
                    )
                )
            }
        }
        
        return suggestions
    }
    
    private fun generateMileageBasedSuggestions(
        vehicle: Vehicle,
        currentMileage: Int
    ): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        
        // Major mileage milestones
        when {
            currentMileage >= 95000 && currentMileage < 105000 -> {
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "100,000 km Service Approaching",
                        description = "Major service milestone approaching. Consider timing belt, spark plugs, and comprehensive inspection.",
                        priority = MaintenancePriority.HIGH,
                        suggestionType = SuggestionType.MILEAGE_BASED,
                        dueMileage = 100000,
                        estimatedCost = 150000.0
                    )
                )
            }
            
            currentMileage >= 45000 && currentMileage < 55000 -> {
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "50,000 km Intermediate Service",
                        description = "Intermediate service due. Check brake pads, fluids, and filters.",
                        priority = MaintenancePriority.MEDIUM,
                        suggestionType = SuggestionType.MILEAGE_BASED,
                        dueMileage = 50000,
                        estimatedCost = 75000.0
                    )
                )
            }
            
            currentMileage >= 90000 -> {
                suggestions.add(
                    MaintenanceSuggestion(
                        vehicleId = vehicle.id,
                        title = "Transmission Fluid Service",
                        description = "High mileage: Consider transmission fluid change if not done recently.",
                        priority = MaintenancePriority.MEDIUM,
                        suggestionType = SuggestionType.MILEAGE_BASED,
                        estimatedCost = 45000.0
                    )
                )
            }
        }
        
        return suggestions
    }
    
    private fun generatePatternAnalysisSuggestions(
        vehicle: Vehicle,
        serviceHistory: List<ServiceRecord>
    ): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        
        if (serviceHistory.size < 3) return suggestions
        
        // Analyze service frequency
        val maintenanceRecords = serviceHistory.filter { it.serviceType == ServiceType.MAINTENANCE }
        if (maintenanceRecords.size >= 3) {
            val avgInterval = calculateAverageServiceInterval(maintenanceRecords)
            val lastService = maintenanceRecords.maxByOrNull { it.date }
            
            lastService?.let { last ->
                val daysSinceLastService = (System.currentTimeMillis() - last.date) / (1000 * 60 * 60 * 24)
                
                if (daysSinceLastService > avgInterval * 1.2) {
                    suggestions.add(
                        MaintenanceSuggestion(
                            vehicleId = vehicle.id,
                            title = "Service Overdue",
                            description = "Based on your service history, you're overdue for maintenance by ${((daysSinceLastService - avgInterval) / 30.0).roundToInt()} months.",
                            priority = MaintenancePriority.HIGH,
                            suggestionType = SuggestionType.PATTERN_ANALYSIS
                        )
                    )
                }
            }
        }
        
        // Analyze cost patterns
        val recentCosts = serviceHistory
            .takeLast(5)
            .map { it.cost.toDouble() }
            .average()
        
        if (recentCosts > 100000) { // NGN 100,000
            suggestions.add(
                MaintenanceSuggestion(
                    vehicleId = vehicle.id,
                    title = "High Maintenance Costs Detected",
                    description = "Your recent service costs are above average. Consider getting multiple quotes or exploring alternative service providers.",
                    priority = MaintenancePriority.MEDIUM,
                    suggestionType = SuggestionType.PATTERN_ANALYSIS
                )
            )
        }
        
        return suggestions
    }
    
    private fun generateWeatherBasedSuggestions(vehicle: Vehicle): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        
        // Placeholder for weather API integration
        // For now, provide general weather-related suggestions
        
        suggestions.add(
            MaintenanceSuggestion(
                vehicleId = vehicle.id,
                title = "Weather Protection Check",
                description = "Check rubber seals, undercoating, and paint protection for weather damage prevention.",
                priority = MaintenancePriority.LOW,
                suggestionType = SuggestionType.WEATHER_BASED,
                actionable = false
            )
        )
        
        return suggestions
    }
    
    private fun generateUrgentSuggestions(
        vehicle: Vehicle,
        maintenanceTasks: List<MaintenanceTask>,
        currentMileage: Int
    ): List<MaintenanceSuggestion> {
        val suggestions = mutableListOf<MaintenanceSuggestion>()
        
        val now = System.currentTimeMillis()
        val sevenDaysFromNow = now + (7 * 24 * 60 * 60 * 1000)
        
        maintenanceTasks.filter { it.isActive }.forEach { task ->
            val isOverdueByDate = task.nextDueDate?.let { it < now } ?: false
            val isDueSoonByDate = task.nextDueDate?.let { it <= sevenDaysFromNow } ?: false
            val isOverdueByMileage = task.nextDueMileage?.let { currentMileage >= it } ?: false
            val isDueSoonByMileage = task.nextDueMileage?.let { currentMileage >= it - 500 } ?: false
            
            when {
                isOverdueByDate || isOverdueByMileage -> {
                    suggestions.add(
                        MaintenanceSuggestion(
                            vehicleId = vehicle.id,
                            title = "Overdue: ${task.taskName}",
                            description = "This maintenance task is overdue. Schedule service immediately to prevent potential damage.",
                            priority = MaintenancePriority.CRITICAL,
                            suggestionType = SuggestionType.URGENT,
                            dueMileage = task.nextDueMileage,
                            dueDate = task.nextDueDate
                        )
                    )
                }
                
                isDueSoonByDate || isDueSoonByMileage -> {
                    suggestions.add(
                        MaintenanceSuggestion(
                            vehicleId = vehicle.id,
                            title = "Due Soon: ${task.taskName}",
                            description = "This maintenance task is coming due soon. Schedule service within the next week.",
                            priority = MaintenancePriority.HIGH,
                            suggestionType = SuggestionType.URGENT,
                            dueMileage = task.nextDueMileage,
                            dueDate = task.nextDueDate
                        )
                    )
                }
            }
        }
        
        return suggestions
    }
    
    private fun calculateAverageServiceInterval(records: List<ServiceRecord>): Int {
        if (records.size < 2) return 180 // Default 6 months
        
        val sortedRecords = records.sortedBy { it.date }
        val intervals = mutableListOf<Long>()
        
        for (i in 1 until sortedRecords.size) {
            val interval = sortedRecords[i].date - sortedRecords[i-1].date
            intervals.add(interval)
        }
        
        return (intervals.average() / (1000 * 60 * 60 * 24)).toInt() // Convert to days
    }
    
    fun getPredefinedMaintenanceTemplates(vehicleType: VehicleType): List<MaintenanceTask> {
        return when (vehicleType) {
            VehicleType.SEDAN -> getSedanMaintenanceTemplates()
            VehicleType.SUV -> getSuvMaintenanceTemplates()
            VehicleType.TRUCK -> getTruckMaintenanceTemplates()
            VehicleType.MOTORCYCLE -> getMotorcycleMaintenanceTemplates()
            VehicleType.OTHER -> getDefaultMaintenanceTemplates()
        }
    }
    
    private fun getSedanMaintenanceTemplates(): List<MaintenanceTask> {
        val now = System.currentTimeMillis()
        return listOf(
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(), // Will be set when assigned to vehicle
                taskName = "Engine Oil & Filter Change",
                description = "Regular oil change to maintain engine health",
                intervalMonths = 6,
                intervalMileageKm = 7500,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Air Filter Replacement",
                description = "Replace air filter for optimal engine performance",
                intervalMonths = 12,
                intervalMileageKm = 20000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.MEDIUM,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Brake Pads Inspection",
                description = "Check brake pads for wear and replace if necessary",
                intervalMonths = 6,
                intervalMileageKm = 10000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Tire Rotation & Pressure Check",
                description = "Rotate tires and check pressure for even wear",
                intervalMonths = 3,
                intervalMileageKm = 5000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.MEDIUM,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Battery Health Check",
                description = "Check battery terminals and charge level",
                intervalMonths = 6,
                intervalMileageKm = null,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.MEDIUM,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            )
        )
    }
    
    private fun getSuvMaintenanceTemplates(): List<MaintenanceTask> {
        val templates = getSedanMaintenanceTemplates().toMutableList()
        val now = System.currentTimeMillis()
        
        // Add SUV-specific tasks
        templates.add(
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Four-Wheel Drive System Check",
                description = "Inspect 4WD system components and fluid levels",
                intervalMonths = 12,
                intervalMileageKm = 20000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.MEDIUM,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            )
        )
        
        return templates
    }
    
    private fun getTruckMaintenanceTemplates(): List<MaintenanceTask> {
        val templates = getSuvMaintenanceTemplates().toMutableList()
        val now = System.currentTimeMillis()
        
        // Add truck-specific tasks
        templates.add(
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Suspension System Inspection",
                description = "Check shocks, springs, and bushings for wear",
                intervalMonths = 6,
                intervalMileageKm = 15000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            )
        )
        
        return templates
    }
    
    private fun getMotorcycleMaintenanceTemplates(): List<MaintenanceTask> {
        val now = System.currentTimeMillis()
        return listOf(
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Engine Oil & Filter Change",
                description = "Regular oil change for motorcycle engine",
                intervalMonths = 4,
                intervalMileageKm = 3000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Chain & Sprocket Inspection",
                description = "Check chain tension and lubrication, inspect sprockets",
                intervalMonths = 2,
                intervalMileageKm = 1000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Brake Fluid Check",
                description = "Check brake fluid level and condition",
                intervalMonths = 6,
                intervalMileageKm = 5000,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.CRITICAL,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            ),
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                taskName = "Tire Pressure & Tread Check",
                description = "Check tire pressure and tread depth weekly",
                intervalMonths = 1,
                intervalMileageKm = 500,
                lastCheckedDate = null,
                lastCheckedMileage = null,
                nextDueDate = null,
                nextDueMileage = null,
                priority = MaintenancePriority.HIGH,
                isCustom = false,
                createdAt = now,
                updatedAt = now
            )
        )
    }
    
    private fun getDefaultMaintenanceTemplates(): List<MaintenanceTask> {
        return getSedanMaintenanceTemplates()
    }
}