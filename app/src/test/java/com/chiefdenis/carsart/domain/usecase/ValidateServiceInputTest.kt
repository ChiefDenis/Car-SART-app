package com.chiefdenis.carsart.domain.usecase

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ValidateServiceInputTest {

    private lateinit var validateServiceInput: ValidateServiceInput

    @BeforeEach
    fun setUp() {
        validateServiceInput = ValidateServiceInput()
    }

    @Test
    fun `validateMileage with valid input should succeed`() {
        val result = validateServiceInput.validateMileage("10000")
        assertTrue(result.successful)
    }

    @Test
    fun `validateMileage with blank input should fail`() {
        val result = validateServiceInput.validateMileage("")
        assertFalse(result.successful)
    }

    @Test
    fun `validateMileage with negative input should fail`() {
        val result = validateServiceInput.validateMileage("-100")
        assertFalse(result.successful)
    }

    @Test
    fun `validateMileage with non-numeric input should fail`() {
        val result = validateServiceInput.validateMileage("abc")
        assertFalse(result.successful)
    }

    @Test
    fun `validateCost with valid input should succeed`() {
        val result = validateServiceInput.validateCost("100.50")
        assertTrue(result.successful)
    }

    @Test
    fun `validateCost with blank input should fail`() {
        val result = validateServiceInput.validateCost("")
        assertFalse(result.successful)
    }

    @Test
    fun `validateCost with negative input should fail`() {
        val result = validateServiceInput.validateCost("-50")
        assertFalse(result.successful)
    }

    @Test
    fun `validateCost with non-numeric input should fail`() {
        val result = validateServiceInput.validateCost("abc")
        assertFalse(result.successful)
    }
}
