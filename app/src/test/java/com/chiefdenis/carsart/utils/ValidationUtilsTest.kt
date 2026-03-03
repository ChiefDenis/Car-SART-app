package com.chiefdenis.carsart.utils

import com.chiefdenis.carsart.data.database.Vehicle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class ValidationUtilsTest {
    
    @Nested
    @DisplayName("Vehicle Nickname Validation")
    inner class VehicleNicknameValidation {
        
        @Test
        @DisplayName("Should accept valid nicknames")
        fun validateVehicleNickname_validNickname_returnsSuccess() {
            // Given
            val validNicknames = listOf("Car", "My Vehicle", "Toyota Camry 2020", "Test-123")
            
            // When & Then
            validNicknames.forEach { nickname ->
                val result = ValidationUtils.validateVehicleNickname(nickname)
                assertTrue(result.isValid, "Should accept valid nickname: $nickname")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject blank nickname")
        fun validateVehicleNickname_blankNickname_returnsError() {
            // Given
            val blankNicknames = listOf("", "   ", "\t", "\n")
            
            // When & Then
            blankNicknames.forEach { nickname ->
                val result = ValidationUtils.validateVehicleNickname(nickname)
                assertFalse(result.isValid, "Should reject blank nickname: '$nickname'")
                assertInstanceOf(ValidationResult.Error::class.java, result)
                assertEquals("Vehicle nickname is required", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject nickname that is too short")
        fun validateVehicleNickname_shortNickname_returnsError() {
            // Given
            val shortNicknames = listOf("A", "B")
            
            // When & Then
            shortNicknames.forEach { nickname ->
                val result = ValidationUtils.validateVehicleNickname(nickname)
                assertFalse(result.isValid, "Should reject short nickname: $nickname")
                assertInstanceOf(ValidationResult.Error::class.java, result)
                assertEquals("Nickname must be at least 2 characters", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject nickname that is too long")
        fun validateVehicleNickname_longNickname_returnsError() {
            // Given
            val longNickname = "A".repeat(51)
            
            // When
            val result = ValidationUtils.validateVehicleNickname(longNickname)
            
            // Then
            assertFalse(result.isValid, "Should reject nickname that is too long")
            assertInstanceOf(ValidationResult.Error::class.java, result)
            assertEquals("Nickname must be less than 50 characters", (result as ValidationResult.Error).message)
        }
    }
    
    @Nested
    @DisplayName("Vehicle Make Validation")
    inner class VehicleMakeValidation {
        
        @Test
        @DisplayName("Should accept valid makes")
        fun validateMake_validMake_returnsSuccess() {
            // Given
            val validMakes = listOf("Toyota", "Honda", "Ford", "BMW", "Mercedes-Benz")
            
            // When & Then
            validMakes.forEach { make ->
                val result = ValidationUtils.validateMake(make)
                assertTrue(result.isValid, "Should accept valid make: $make")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject blank make")
        fun validateMake_blankMake_returnsError() {
            // Given
            val blankMakes = listOf("", "   ")
            
            // When & Then
            blankMakes.forEach { make ->
                val result = ValidationUtils.validateMake(make)
                assertFalse(result.isValid, "Should reject blank make: '$make'")
                assertEquals("Make is required", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject make that is too short")
        fun validateMake_shortMake_returnsError() {
            // Given
            val shortMakes = listOf("A", "B")
            
            // When & Then
            shortMakes.forEach { make ->
                val result = ValidationUtils.validateMake(make)
                assertFalse(result.isValid, "Should reject short make: $make")
                assertEquals("Make must be at least 2 characters", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject make that is too long")
        fun validateMake_longMake_returnsError() {
            // Given
            val longMake = "A".repeat(31)
            
            // When
            val result = ValidationUtils.validateMake(longMake)
            
            // Then
            assertFalse(result.isValid, "Should reject make that is too long")
            assertEquals("Make must be less than 30 characters", (result as ValidationResult.Error).message)
        }
    }
    
    @Nested
    @DisplayName("Vehicle Model Validation")
    inner class VehicleModelValidation {
        
        @Test
        @DisplayName("Should accept valid models")
        fun validateModel_validModel_returnsSuccess() {
            // Given
            val validModels = listOf("Camry", "Civic", "Mustang", "X5", "C-Class")
            
            // When & Then
            validModels.forEach { model ->
                val result = ValidationUtils.validateModel(model)
                assertTrue(result.isValid, "Should accept valid model: $model")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject blank model")
        fun validateModel_blankModel_returnsError() {
            // Given
            val blankModels = listOf("", "   ")
            
            // When & Then
            blankModels.forEach { model ->
                val result = ValidationUtils.validateModel(model)
                assertFalse(result.isValid, "Should reject blank model: '$model'")
                assertEquals("Model is required", (result as ValidationResult.Error).message)
            }
        }
    }
    
    @Nested
    @DisplayName("Vehicle Year Validation")
    inner class VehicleYearValidation {
        
        @Test
        @DisplayName("Should accept valid years")
        fun validateYear_validYear_returnsSuccess() {
            // Given
            val validYears = listOf(1900, 2000, 2020, 2024)
            
            // When & Then
            validYears.forEach { year ->
                val result = ValidationUtils.validateYear(year)
                assertTrue(result.isValid, "Should accept valid year: $year")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject year before 1900")
        fun validateYear_yearBefore1900_returnsError() {
            // Given
            val invalidYears = listOf(1899, 1800, 0)
            
            // When & Then
            invalidYears.forEach { year ->
                val result = ValidationUtils.validateYear(year)
                assertFalse(result.isValid, "Should reject year before 1900: $year")
                assertEquals("Year must be after 1900", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject year in distant future")
        fun validateYear_distantFuture_returnsError() {
            // Given
            val currentYear = java.time.Year.now().value
            val futureYear = currentYear + 10
            
            // When
            val result = ValidationUtils.validateYear(futureYear)
            
            // Then
            assertFalse(result.isValid, "Should reject year in distant future: $futureYear")
            assertEquals("Year cannot be in distant future", (result as ValidationResult.Error).message)
        }
    }
    
    @Nested
    @DisplayName("Mileage Validation")
    inner class MileageValidation {
        
        @Test
        @DisplayName("Should accept valid mileage")
        fun validateMileage_validMileage_returnsSuccess() {
            // Given
            val validMileages = listOf(0, 5000, 50000, 200000)
            
            // When & Then
            validMileages.forEach { mileage ->
                val result = ValidationUtils.validateMileage(mileage)
                assertTrue(result.isValid, "Should accept valid mileage: $mileage")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject negative mileage")
        fun validateMileage_negativeMileage_returnsError() {
            // Given
            val negativeMileages = listOf(-1, -1000)
            
            // When & Then
            negativeMileages.forEach { mileage ->
                val result = ValidationUtils.validateMileage(mileage)
                assertFalse(result.isValid, "Should reject negative mileage: $mileage")
                assertEquals("Mileage cannot be negative", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject extremely high mileage")
        fun validateMileage_extremelyHighMileage_returnsError() {
            // Given
            val highMileage = 10000001
            
            // When
            val result = ValidationUtils.validateMileage(highMileage)
            
            // Then
            assertFalse(result.isValid, "Should reject extremely high mileage")
            assertEquals("Mileage seems too high", (result as ValidationResult.Error).message)
        }
    }
    
    @Nested
    @DisplayName("Cost Validation")
    inner class CostValidation {
        
        @Test
        @DisplayName("Should accept valid costs")
        fun validateCost_validCost_returnsSuccess() {
            // Given
            val validCosts = listOf(0.0, 50.0, 1000.0, 50000.0)
            
            // When & Then
            validCosts.forEach { cost ->
                val result = ValidationUtils.validateCost(cost)
                assertTrue(result.isValid, "Should accept valid cost: $cost")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject negative costs")
        fun validateCost_negativeCost_returnsError() {
            // Given
            val negativeCosts = listOf(-1.0, -100.0)
            
            // When & Then
            negativeCosts.forEach { cost ->
                val result = ValidationUtils.validateCost(cost)
                assertFalse(result.isValid, "Should reject negative cost: $cost")
                assertEquals("Cost cannot be negative", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject extremely high costs")
        fun validateCost_extremelyHighCost_returnsError() {
            // Given
            val highCost = 10000001.0
            
            // When
            val result = ValidationUtils.validateCost(highCost)
            
            // Then
            assertFalse(result.isValid, "Should reject extremely high cost")
            assertEquals("Cost seems too high", (result as ValidationResult.Error).message)
        }
    }
    
    @Nested
    @DisplayName("VIN Validation")
    inner class VINValidation {
        
        @Test
        @DisplayName("Should accept valid VIN")
        fun validateVIN_validVIN_returnsSuccess() {
            // Given
            val validVIN = "12345678901234567"
            
            // When
            val result = ValidationUtils.validateVIN(validVIN)
            
            // Then
            assertTrue(result.isValid, "Should accept valid VIN")
            assertInstanceOf(ValidationResult.Success::class.java, result)
        }
        
        @Test
        @DisplayName("Should accept null VIN (optional field)")
        fun validateVIN_nullVIN_returnsSuccess() {
            // When
            val result = ValidationUtils.validateVIN(null)
            
            // Then
            assertTrue(result.isValid, "Should accept null VIN as optional field")
            assertInstanceOf(ValidationResult.Success::class.java, result)
        }
        
        @Test
        @DisplayName("Should accept blank VIN (optional field)")
        fun validateVIN_blankVIN_returnsSuccess() {
            // Given
            val blankVINs = listOf("", "   ")
            
            // When & Then
            blankVINs.forEach { vin ->
                val result = ValidationUtils.validateVIN(vin)
                assertTrue(result.isValid, "Should accept blank VIN as optional field: '$vin'")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject VIN with incorrect length")
        fun validateVIN_incorrectLength_returnsError() {
            // Given
            val invalidVINs = listOf("123", "123456789012345678")
            
            // When & Then
            invalidVINs.forEach { vin ->
                val result = ValidationUtils.validateVIN(vin)
                assertFalse(result.isValid, "Should reject VIN with incorrect length: $vin")
                assertEquals("VIN must be exactly 17 characters", (result as ValidationResult.Error).message)
            }
        }
        
        @Test
        @DisplayName("Should reject VIN with invalid characters")
        fun validateVIN_invalidCharacters_returnsError() {
            // Given
            val invalidVINs = listOf("1234567890123456!", "ABCDEFGHIJKLMNOPQ", "1234567890123456 ")
            
            // When & Then
            invalidVINs.forEach { vin ->
                val result = ValidationUtils.validateVIN(vin)
                assertFalse(result.isValid, "Should reject VIN with invalid characters: $vin")
                assertEquals("VIN can only contain letters and numbers", (result as ValidationResult.Error).message)
            }
        }
    }
    
    @Nested
    @DisplayName("License Plate Validation")
    inner class LicensePlateValidation {
        
        @Test
        @DisplayName("Should accept valid license plates")
        fun validateLicensePlate_validLicensePlate_returnsSuccess() {
            // Given
            val validLicensePlates = listOf("ABC-123", "XYZ 999", "12345", "AB-12-CD")
            
            // When & Then
            validLicensePlates.forEach { plate ->
                val result = ValidationUtils.validateLicensePlate(plate)
                assertTrue(result.isValid, "Should accept valid license plate: $plate")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should accept null license plate (optional field)")
        fun validateLicensePlate_nullLicensePlate_returnsSuccess() {
            // When
            val result = ValidationUtils.validateLicensePlate(null)
            
            // Then
            assertTrue(result.isValid, "Should accept null license plate as optional field")
            assertInstanceOf(ValidationResult.Success::class.java, result)
        }
        
        @Test
        @DisplayName("Should accept blank license plate (optional field)")
        fun validateLicensePlate_blankLicensePlate_returnsSuccess() {
            // Given
            val blankLicensePlates = listOf("", "   ")
            
            // When & Then
            blankLicensePlates.forEach { plate ->
                val result = ValidationUtils.validateLicensePlate(plate)
                assertTrue(result.isValid, "Should accept blank license plate as optional field: '$plate'")
                assertInstanceOf(ValidationResult.Success::class.java, result)
            }
        }
        
        @Test
        @DisplayName("Should reject license plate that is too long")
        fun validateLicensePlate_tooLong_returnsError() {
            // Given
            val longLicensePlate = "ABCDEFGHIJKLMNO" // 16 characters
            
            // When
            val result = ValidationUtils.validateLicensePlate(longLicensePlate)
            
            // Then
            assertFalse(result.isValid, "Should reject license plate that is too long")
            assertEquals("License plate must be less than 15 characters", (result as ValidationResult.Error).message)
        }
        
        @Test
        @DisplayName("Should reject license plate with invalid characters")
        fun validateLicensePlate_invalidCharacters_returnsError() {
            // Given
            val invalidLicensePlates = listOf("ABC@123", "XYZ#999", "123!456")
            
            // When & Then
            invalidLicensePlates.forEach { plate ->
                val result = ValidationUtils.validateLicensePlate(plate)
                assertFalse(result.isValid, "Should reject license plate with invalid characters: $plate")
                assertEquals("License plate contains invalid characters", (result as ValidationResult.Error).message)
            }
        }
    }
}
