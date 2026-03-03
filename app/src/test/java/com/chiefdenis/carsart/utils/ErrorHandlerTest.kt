package com.chiefdenis.carsart.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import android.content.Context

class ErrorHandlerTest {
    
    private lateinit var errorHandler: ErrorHandler
    private lateinit var mockContext: Context
    
    @BeforeEach
    fun setup() {
        mockContext = mock()
        errorHandler = ErrorHandler(mockContext)
    }
    
    @Nested
    @DisplayName("Exception Parsing")
    inner class ExceptionParsing {
        
        @Test
        @DisplayName("Should parse CarSartError correctly")
        fun parseException_carSartError_returnsSameError() {
            // Given
            val originalError = CarSartError.ValidationError("Test validation error")
            
            // When
            val parsedError = errorHandler.parseException(originalError)
            
            // Then
            assertEquals(originalError, parsedError, "Should return the same CarSartError instance")
        }
        
        @Test
        @DisplayName("Should parse UnknownHostException as NetworkError")
        fun parseException_unknownHostException_returnsNetworkError() {
            // Given
            val exception = UnknownHostException("No internet")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.NetworkError::class.java, parsedError)
            assertEquals("Network connection error", parsedError.message)
            assertEquals(exception, parsedError.cause)
        }
        
        @Test
        @DisplayName("Should parse SocketTimeoutException as NetworkError")
        fun parseException_socketTimeoutException_returnsNetworkError() {
            // Given
            val exception = SocketTimeoutException("Connection timed out")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.NetworkError::class.java, parsedError)
            assertEquals("Network connection error", parsedError.message)
            assertEquals(exception, parsedError.cause)
        }
        
        @Test
        @DisplayName("Should parse IOException as FileOperationError")
        fun parseException_ioException_returnsFileOperationError() {
            // Given
            val exception = IOException("File not found")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.FileOperationError::class.java, parsedError)
            assertEquals("File operation failed", parsedError.message)
            assertEquals(exception, parsedError.cause)
        }
        
        @Test
        @DisplayName("Should parse SecurityException as PermissionError")
        fun parseException_securityException_returnsPermissionError() {
            // Given
            val exception = SecurityException("Permission denied")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.PermissionError::class.java, parsedError)
            assertEquals("Permission denied", parsedError.message)
        }
        
        @Test
        @DisplayName("Should parse IllegalArgumentException as ValidationError")
        fun parseException_illegalArgumentException_returnsValidationError() {
            // Given
            val exception = IllegalArgumentException("Invalid input")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.ValidationError::class.java, parsedError)
            assertEquals("Invalid input: Invalid input", parsedError.message)
        }
        
        @Test
        @DisplayName("Should parse generic Exception as UnknownError")
        fun parseException_genericException_returnsUnknownError() {
            // Given
            val exception = RuntimeException("Something went wrong")
            
            // When
            val parsedError = errorHandler.parseException(exception)
            
            // Then
            assertInstanceOf(CarSartError.UnknownError::class.java, parsedError)
            assertEquals("An unexpected error occurred", parsedError.message)
            assertEquals(exception, parsedError.cause)
        }
    }
    
    @Nested
    @DisplayName("Error Message Generation")
    inner class ErrorMessageGeneration {
        
        @Test
        @DisplayName("Should generate correct message for NetworkError")
        fun getErrorMessage_networkError_returnsCorrectMessage() {
            // Given
            val error = CarSartError.NetworkError("Connection failed")
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals("Network error. Please check your internet connection.", message)
        }
        
        @Test
        @DisplayName("Should generate correct message for DatabaseError")
        fun getErrorMessage_databaseError_returnsCorrectMessage() {
            // Given
            val error = CarSartError.DatabaseError("Database locked")
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals("Database error. Please try again.", message)
        }
        
        @Test
        @DisplayName("Should return original message for ValidationError")
        fun getErrorMessage_validationError_returnsOriginalMessage() {
            // Given
            val originalMessage = "Field cannot be empty"
            val error = CarSartError.ValidationError(originalMessage)
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals(originalMessage, message)
        }
        
        @Test
        @DisplayName("Should generate correct message for FileOperationError")
        fun getErrorMessage_fileOperationError_returnsCorrectMessage() {
            // Given
            val error = CarSartError.FileOperationError("Cannot write file")
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals("File operation failed. Please check storage permissions.", message)
        }
        
        @Test
        @DisplayName("Should generate correct message for PermissionError")
        fun getErrorMessage_permissionError_returnsCorrectMessage() {
            // Given
            val error = CarSartError.PermissionError("Camera permission required")
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals("Permission required. Please grant the necessary permissions.", message)
        }
        
        @Test
        @DisplayName("Should generate correct message for UnknownError")
        fun getErrorMessage_unknownError_returnsCorrectMessage() {
            // Given
            val error = CarSartError.UnknownError("Unexpected error")
            
            // When
            val message = errorHandler.getErrorMessage(error)
            
            // Then
            assertEquals("An unexpected error occurred. Please try again.", message)
        }
    }
    
    @Nested
    @DisplayName("Result Wrapper")
    inner class ResultWrapper {
        
        @Test
        @DisplayName("Should create Success result correctly")
        fun success_resultPropertiesCorrect() {
            // Given
            val data = "test data"
            val result = Result.Success(data)
            
            // When & Then
            assertTrue(result.isSuccess, "Success result should be successful")
            assertFalse(result.isError, "Success result should not be an error")
            assertFalse(result.isLoading, "Success result should not be loading")
            assertEquals(data, result.getOrNull(), "Should return correct data")
            assertEquals(data, result.getOrThrow(), "Should return correct data when thrown")
        }
        
        @Test
        @DisplayName("Should create Error result correctly")
        fun error_resultPropertiesCorrect() {
            // Given
            val error = CarSartError.ValidationError("Test error")
            val result = Result.Error(error)
            
            // When & Then
            assertFalse(result.isSuccess, "Error result should not be successful")
            assertTrue(result.isError, "Error result should be an error")
            assertFalse(result.isLoading, "Error result should not be loading")
            assertNull(result.getOrNull(), "Should return null for error result")
            assertThrows<CarSartError> { result.getOrThrow() }
        }
        
        @Test
        @DisplayName("Should create Loading result correctly")
        fun loading_resultPropertiesCorrect() {
            // Given
            val result = Result.Loading
            
            // When & Then
            assertFalse(result.isSuccess, "Loading result should not be successful")
            assertFalse(result.isError, "Loading result should not be an error")
            assertTrue(result.isLoading, "Loading result should be loading")
            assertNull(result.getOrNull(), "Should return null for loading result")
            assertThrows<IllegalStateException> { result.getOrThrow() }
        }
        
        @Test
        @DisplayName("Should execute onSuccess callback for Success result")
        fun success_onSuccess_executesCallback() {
            // Given
            val result = Result.Success("test data")
            var callbackExecuted = false
            var callbackData: String? = null
            
            // When
            result.onSuccess { data ->
                callbackExecuted = true
                callbackData = data
            }
            
            // Then
            assertTrue(callbackExecuted, "onSuccess callback should be executed")
            assertEquals("test data", callbackData, "Callback should receive correct data")
        }
        
        @Test
        @DisplayName("Should execute onError callback for Error result")
        fun error_onError_executesCallback() {
            // Given
            val error = CarSartError.ValidationError("Test error")
            val result = Result.Error(error)
            var callbackExecuted = false
            var callbackError: CarSartError? = null
            
            // When
            result.onError { err ->
                callbackExecuted = true
                callbackError = err
            }
            
            // Then
            assertTrue(callbackExecuted, "onError callback should be executed")
            assertEquals(error, callbackError, "Callback should receive correct error")
        }
        
        @Test
        @DisplayName("Should execute onLoading callback for Loading result")
        fun loading_onLoading_executesCallback() {
            // Given
            val result = Result.Loading
            var callbackExecuted = false
            
            // When
            result.onLoading {
                callbackExecuted = true
            }
            
            // Then
            assertTrue(callbackExecuted, "onLoading callback should be executed")
        }
        
        @Test
        @DisplayName("Should chain callbacks correctly")
        fun result_chaining_worksCorrectly() {
            // Given
            val result = Result.Success("test data")
            var successCalled = false
            var errorCalled = false
            var loadingCalled = false
            
            // When
            result
                .onSuccess { successCalled = true }
                .onError { errorCalled = true }
                .onLoading { loadingCalled = true }
            
            // Then
            assertTrue(successCalled, "Success callback should be called")
            assertFalse(errorCalled, "Error callback should not be called")
            assertFalse(loadingCalled, "Loading callback should not be called")
        }
    }
}
