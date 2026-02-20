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

class DeleteVehicleTest {

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var deleteVehicle: DeleteVehicle

    @BeforeEach
    fun setUp() {
        vehicleRepository = mockk(relaxed = true)
        deleteVehicle = DeleteVehicle(vehicleRepository)
    }

    @Test
    fun `invoke with existing vehicle should delete vehicle`() = runBlocking {
        // Given
        val vehicleId = UUID.randomUUID()
        coEvery { vehicleRepository.getVehicleById(vehicleId) } returns mockk<Vehicle>()

        // When
        val result = deleteVehicle(vehicleId)

        // Then
        assertTrue(result.isSuccess)
        coVerify { vehicleRepository.deleteVehicleById(vehicleId) }
    }

    @Test
    fun `invoke with non-existing vehicle should fail`() = runBlocking {
        // Given
        val vehicleId = UUID.randomUUID()
        coEvery { vehicleRepository.getVehicleById(vehicleId) } returns null

        // When
        val result = deleteVehicle(vehicleId)

        // Then
        assertTrue(result.isFailure)
    }
}
