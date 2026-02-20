package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class UpdateMileageTest {

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var updateMileage: UpdateMileage

    @BeforeEach
    fun setUp() {
        vehicleRepository = mockk(relaxed = true)
        updateMileage = UpdateMileage(vehicleRepository)
    }

    @Test
    fun `invoke with valid mileage should update mileage`() = runBlocking {
        // Given
        val vehicleId = UUID.randomUUID()
        val currentMileage = 10000
        val newMileage = 11000
        val vehicle = Vehicle(id = vehicleId, nickname = "Test", make = "Test", model = "Test", year = 2022, currentMileage = currentMileage)
        coEvery { vehicleRepository.getVehicleById(vehicleId) } returns vehicle

        // When
        val result = updateMileage(vehicleId, newMileage)

        // Then
        assertTrue(result.isSuccess)
        coVerify { vehicleRepository.updateMileage(vehicleId, newMileage) }
    }

    @Test
    fun `invoke with lower mileage should fail`() = runBlocking {
        // Given
        val vehicleId = UUID.randomUUID()
        val currentMileage = 10000
        val newMileage = 9000
        val vehicle = Vehicle(id = vehicleId, nickname = "Test", make = "Test", model = "Test", year = 2022, currentMileage = currentMileage)
        coEvery { vehicleRepository.getVehicleById(vehicleId) } returns vehicle

        // When
        val result = updateMileage(vehicleId, newMileage)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with negative mileage should fail`() = runBlocking {
        // Given
        val vehicleId = UUID.randomUUID()
        val newMileage = -100
        val vehicle = Vehicle(id = vehicleId, nickname = "Test", make = "Test", model = "Test", year = 2022, currentMileage = 10000)
        coEvery { vehicleRepository.getVehicleById(vehicleId) } returns vehicle

        // When
        val result = updateMileage(vehicleId, newMileage)

        // Then
        assertTrue(result.isFailure)
    }
}
