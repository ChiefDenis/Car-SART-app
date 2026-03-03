package com.chiefdenis.carsart.domain

import com.chiefdenis.carsart.data.database.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

class SuggestionEngineTest {
    
    private lateinit var suggestionEngine: SuggestionEngine
    private lateinit var testVehicle: Vehicle
    private lateinit var testServiceRecords: List<ServiceRecord>
    private lateinit var testMaintenanceTasks: List<MaintenanceTask>
    
    @BeforeEach
    fun setup() {
        suggestionEngine = SuggestionEngine()
        
        testVehicle = Vehicle(
            id = UUID.randomUUID(),
            nickname = "Test Car",
            make = "Toyota",
            model = "Camry",
            year = 2020,
            vin = "12345678901234567",
            licensePlate = "ABC-123",
            currentMileage = 50000,
            purchaseDate = System.currentTimeMillis() - 86400000L * 365, // 1 year ago
            vehicleType = VehicleType.SEDAN
        )
        
        testServiceRecords = listOf(
            ServiceRecord(
                id = UUID.randomUUID(),
                vehicleId = testVehicle.id,
                date = System.currentTimeMillis() - 86400000L * 30, // 30 days ago
                mileage = 45000,
                serviceType = ServiceType.MAINTENANCE,
                provider = "Auto Shop",
                cost = java.math.BigDecimal("50000.00"),
                notes = "Oil change"
            ),
            ServiceRecord(
                id = UUID.randomUUID(),
                vehicleId = testVehicle.id,
                date = System.currentTimeMillis() - 86400000L * 60, // 60 days ago
                mileage = 40000,
                serviceType = ServiceType.MAINTENANCE,
                provider = "Auto Shop",
                cost = java.math.BigDecimal("75000.00"),
                notes = "Tire rotation"
            )
        )
        
        testMaintenanceTasks = listOf(
            MaintenanceTask(
                id = UUID.randomUUID(),
                vehicleId = testVehicle.id,
                taskName = "Oil Change",
                description = "Regular oil change",
                intervalMonths = 6,
                intervalMileageKm = 7500,
                lastCheckedDate = System.currentTimeMillis() - 86400000L * 30,
                lastCheckedMileage = 45000,
                nextDueDate = System.currentTimeMillis() + 86400000L * 150, // 150 days from now
                nextDueMileage = 52500,
                priority = MaintenancePriority.HIGH,
                isCustom = false
            )
        )
    }
    
    @Test
    @DisplayName("Should generate suggestions for vehicle with basic data")
    fun generateSuggestions_basicData_returnsSuggestions() {
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            testServiceRecords,
            testMaintenanceTasks
        )
        
        // Then
        assertTrue(suggestions.isNotEmpty(), "Should generate at least one suggestion")
        assertTrue(suggestions.any { it.suggestionType == SuggestionType.SEASONAL }, "Should include seasonal suggestions")
        assertTrue(suggestions.any { it.suggestionType == SuggestionType.URGENT }, "Should include urgent suggestions")
    }
    
    @Test
    @DisplayName("Should generate mileage-based suggestions for high mileage vehicles")
    fun generateSuggestions_highMileage_returnsMileageSuggestions() {
        // Given
        val highMileageVehicle = testVehicle.copy(currentMileage = 98000)
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            highMileageVehicle,
            testServiceRecords,
            testMaintenanceTasks
        )
        
        // Then
        assertTrue(
            suggestions.any { it.title.contains("100,000") },
            "Should suggest 100,000 km service for high mileage vehicle"
        )
    }
    
    @Test
    @DisplayName("Should generate overdue maintenance suggestions")
    fun generateSuggestions_overdueMaintenance_returnsUrgentSuggestions() {
        // Given
        val overdueTask = testMaintenanceTasks.first().copy(
            nextDueDate = System.currentTimeMillis() - 86400000L, // Yesterday
            priority = MaintenancePriority.CRITICAL
        )
        val overdueTasks = listOf(overdueTask)
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            testServiceRecords,
            overdueTasks
        )
        
        // Then
        assertTrue(
            suggestions.any { it.title.contains("Overdue") && it.priority == MaintenancePriority.CRITICAL },
            "Should generate overdue suggestion with critical priority"
        )
    }
    
    @Test
    @DisplayName("Should generate pattern analysis suggestions for irregular service history")
    fun generateSuggestions_irregularServiceHistory_returnsPatternSuggestions() {
        // Given
        val oldServiceRecord = ServiceRecord(
            id = UUID.randomUUID(),
            vehicleId = testVehicle.id,
            date = System.currentTimeMillis() - 86400000L * 300, // 300 days ago
            mileage = 20000,
            serviceType = ServiceType.MAINTENANCE,
            provider = "Auto Shop",
            cost = java.math.BigDecimal("50000.00")
        )
        val extendedServiceHistory = testServiceRecords + oldServiceRecord
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            extendedServiceHistory,
            testMaintenanceTasks
        )
        
        // Then
        assertTrue(
            suggestions.any { it.suggestionType == SuggestionType.PATTERN_ANALYSIS },
            "Should include pattern analysis suggestions"
        )
    }
    
    @ParameterizedTest
    @EnumSource(VehicleType::class)
    @DisplayName("Should generate maintenance templates for all vehicle types")
    fun getPredefinedMaintenanceTemplates_allVehicleTypes_returnsTemplates(vehicleType: VehicleType) {
        // When
        val templates = suggestionEngine.getPredefinedMaintenanceTemplates(vehicleType)
        
        // Then
        assertTrue(templates.isNotEmpty(), "Should generate templates for $vehicleType")
        assertTrue(
            templates.all { it.intervalMonths > 0 },
            "All templates should have valid interval months"
        )
        assertTrue(
            templates.all { !it.taskName.isBlank() },
            "All templates should have valid task names"
        )
    }
    
    @Test
    @DisplayName("Should include SUV-specific tasks for SUV vehicles")
    fun getPredefinedMaintenanceTemplates_suv_returnsSuvSpecificTasks() {
        // When
        val templates = suggestionEngine.getPredefinedMaintenanceTemplates(VehicleType.SUV)
        
        // Then
        assertTrue(
            templates.any { it.taskName.contains("4WD") || it.taskName.contains("Four-Wheel") },
            "Should include 4WD system check for SUVs"
        )
    }
    
    @Test
    @DisplayName("Should include motorcycle-specific tasks for motorcycles")
    fun getPredefinedMaintenanceTemplates_motorcycle_returnsMotorcycleSpecificTasks() {
        // When
        val templates = suggestionEngine.getPredefinedMaintenanceTemplates(VehicleType.MOTORCYCLE)
        
        // Then
        assertTrue(
            templates.any { it.taskName.contains("Chain") },
            "Should include chain inspection for motorcycles"
        )
        assertTrue(
            templates.any { it.taskName.contains("Brake Fluid") },
            "Should include brake fluid check for motorcycles"
        )
    }
    
    @Test
    @DisplayName("Should sort suggestions by priority")
    fun generateSuggestions_multipleSuggestions_sortedByPriority() {
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            testServiceRecords,
            testMaintenanceTasks
        )
        
        // Then
        if (suggestions.size > 1) {
            for (i in 0 until suggestions.size - 1) {
                assertTrue(
                    suggestions[i].priority.ordinal >= suggestions[i + 1].priority.ordinal,
                    "Suggestions should be sorted by priority (highest first)"
                )
            }
        }
    }
    
    @Test
    @DisplayName("Should handle empty service history gracefully")
    fun generateSuggestions_emptyServiceHistory_returnsSeasonalSuggestions() {
        // Given
        val emptyServiceHistory = emptyList<ServiceRecord>()
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            emptyServiceHistory,
            testMaintenanceTasks
        )
        
        // Then
        assertTrue(suggestions.isNotEmpty(), "Should generate suggestions even with empty service history")
        assertTrue(
            suggestions.any { it.suggestionType == SuggestionType.SEASONAL },
            "Should include seasonal suggestions"
        )
    }
    
    @Test
    @DisplayName("Should handle empty maintenance tasks gracefully")
    fun generateSuggestions_emptyMaintenanceTasks_returnsOtherSuggestions() {
        // Given
        val emptyMaintenanceTasks = emptyList<MaintenanceTask>()
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            testServiceRecords,
            emptyMaintenanceTasks
        )
        
        // Then
        assertTrue(suggestions.isNotEmpty(), "Should generate suggestions even with empty maintenance tasks")
        assertFalse(
            suggestions.any { it.suggestionType == SuggestionType.URGENT },
            "Should not include urgent suggestions when no maintenance tasks exist"
        )
    }
    
    @Test
    @DisplayName("Should generate cost analysis suggestions for high service costs")
    fun generateSuggestions_highServiceCosts_returnsCostAnalysisSuggestions() {
        // Given
        val expensiveServiceRecords = listOf(
            ServiceRecord(
                id = UUID.randomUUID(),
                vehicleId = testVehicle.id,
                date = System.currentTimeMillis() - 86400000L * 30,
                mileage = 45000,
                serviceType = ServiceType.REPAIR,
                provider = "Expensive Shop",
                cost = java.math.BigDecimal("150000.00") // High cost
            )
        )
        
        // When
        val suggestions = suggestionEngine.generateSuggestions(
            testVehicle,
            expensiveServiceRecords,
            testMaintenanceTasks
        )
        
        // Then
        assertTrue(
            suggestions.any { it.title.contains("High Maintenance Costs") },
            "Should suggest cost analysis for high service costs"
        )
    }
}
