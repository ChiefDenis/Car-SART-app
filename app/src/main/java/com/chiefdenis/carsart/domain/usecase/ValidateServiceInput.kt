package com.chiefdenis.carsart.domain.usecase

import java.math.BigDecimal
import javax.inject.Inject

class ValidateServiceInput @Inject constructor() {
    fun validateMileage(mileage: String): ValidationResult {
        if (mileage.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "Mileage cannot be empty.")
        }
        val mileageAsInt = mileage.toIntOrNull()
        if (mileageAsInt == null || mileageAsInt < 0) {
            return ValidationResult(successful = false, errorMessage = "Invalid mileage.")
        }
        return ValidationResult(successful = true)
    }

    fun validateCost(cost: String): ValidationResult {
        if (cost.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "Cost cannot be empty.")
        }
        val costAsBigDecimal = cost.toBigDecimalOrNull()
        if (costAsBigDecimal == null || costAsBigDecimal < BigDecimal.ZERO) {
            return ValidationResult(successful = false, errorMessage = "Invalid cost.")
        }
        return ValidationResult(successful = true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
