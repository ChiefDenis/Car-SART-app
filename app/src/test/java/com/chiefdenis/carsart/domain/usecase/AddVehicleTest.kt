package com.chiefdenis.carsart.domain.usecase

import com.chiefdenis.carsart.data.database.VehicleType
import com.chiefdenis.carsart.data.repository.VehicleRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddVehicleTest {

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var addVehicle: AddVehicle

    @BeforeEach
    fun setUp() {
        vehicleRepository = mockk(relaxed = true)
        addVehicle = AddVehicle(vehicleRepository)
    }

    @Test
    fun `invoke with valid data should add vehicle`() = runBlocking {
        // Given
        val nickname = "My Car"
        val make = "Toyota"
        val model = "Corolla"
        val year = 2020
        val mileage = 10000

        // When
        val result = addVehicle(
            nickname = nickname,
            make = make,
            model = model,
            year = year,
            vin = null,
            licensePlate = null,
            currentMileage = mileage,
            photoUri = null,
            vehicleType = VehicleType.SEDAN
        )

        // Then
        assertTrue(result.isSuccess)
        coVerify { vehicleRepository.addVehicle(any()) }
    }

    @Test
    fun `invoke with blank nickname should fail`() = runBlocking {
        // When
        val result = addVehicle(
            nickname = "",
            make = "Toyota",
            model = "Corolla",
            year = 2020,
            vin = null,
            licensePlate = null,
            currentMileage = 10000,
            photoUri = null,
            vehicleType = VehicleType.SEDAN
        )

        // Then
        assertTrue(result.isFailure)
    }
}
