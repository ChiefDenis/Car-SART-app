package com.chiefdenis.carsart.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

// Error types for better categorization
sealed class CarSartError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    class NetworkError(message: String, cause: Throwable? = null) : CarSartError(message, cause)
    class DatabaseError(message: String, cause: Throwable? = null) : CarSartError(message, cause)
    class ValidationError(message: String) : CarSartError(message)
    class FileOperationError(message: String, cause: Throwable? = null) : CarSartError(message, cause)
    class PermissionError(message: String) : CarSartError(message)
    class UnknownError(message: String, cause: Throwable? = null) : CarSartError(message, cause)
}

// Result wrapper for better error handling
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: CarSartError) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (CarSartError) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }
}

// Error handler utility class
class ErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun parseException(throwable: Throwable): CarSartError {
        return when (throwable) {
            is CarSartError -> throwable
            is UnknownHostException, is SocketTimeoutException -> 
                CarSartError.NetworkError("Network connection error", throwable)
            is IOException -> 
                CarSartError.FileOperationError("File operation failed", throwable)
            is SecurityException -> 
                CarSartError.PermissionError("Permission denied")
            is IllegalArgumentException -> 
                CarSartError.ValidationError("Invalid input: ${throwable.message}")
            else -> 
                CarSartError.UnknownError("An unexpected error occurred", throwable)
        }
    }
    
    fun getErrorMessage(error: CarSartError): String {
        return when (error) {
            is CarSartError.NetworkError -> "Network error. Please check your internet connection."
            is CarSartError.DatabaseError -> "Database error. Please try again."
            is CarSartError.ValidationError -> error.message ?: "Validation error"
            is CarSartError.FileOperationError -> "File operation failed. Please check storage permissions."
            is CarSartError.PermissionError -> "Permission required. Please grant the necessary permissions."
            is CarSartError.UnknownError -> "An unexpected error occurred. Please try again."
        }
    }
    
    fun showToast(error: CarSartError) {
        Toast.makeText(context, getErrorMessage(error), Toast.LENGTH_LONG).show()
    }
    
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

// Extension functions for Flow error handling
fun <T> Flow<T>.catchErrors(): Flow<Result<T>> = flow {
    try {
        collect { value ->
            emit(Result.Success(value))
        }
    } catch (e: Exception) {
        emit(Result.Error(parseException(e)))
    }
}

// Standalone parseException function
fun parseException(throwable: Throwable): CarSartError {
    return when (throwable) {
        is CarSartError -> throwable
        is UnknownHostException, is SocketTimeoutException -> 
            CarSartError.NetworkError("Network connection error", throwable)
        is IOException -> 
            CarSartError.FileOperationError("File operation failed", throwable)
        is SecurityException -> 
            CarSartError.PermissionError("Permission denied")
        is IllegalArgumentException -> 
            CarSartError.ValidationError("Invalid input: ${throwable.message}")
        else -> 
            CarSartError.UnknownError("An unexpected error occurred", throwable)
    }
}

// Safe execute function
suspend fun <T> safeExecute(
    operation: suspend () -> T
): Result<T> {
    return try {
        val result = operation()
        Result.Success(result)
    } catch (e: Exception) {
        Result.Error(parseException(e))
    }
}

// Composable for showing error messages
@Composable
fun ErrorHandler(
    snackbarHostState: SnackbarHostState,
    error: CarSartError?,
    onDismiss: () -> Unit = {}
) {
    LaunchedEffect(error) {
        error?.let { err ->
            val message = when (err) {
                is CarSartError.NetworkError -> "Network error. Please check your internet connection."
                is CarSartError.DatabaseError -> "Database error. Please try again."
                is CarSartError.ValidationError -> err.message ?: "Validation error"
                is CarSartError.FileOperationError -> "File operation failed. Please check storage permissions."
                is CarSartError.PermissionError -> "Permission required. Please grant the necessary permissions."
                is CarSartError.UnknownError -> "An unexpected error occurred. Please try again."
            }
            
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (err is CarSartError.ValidationError) "Fix" else "Retry",
                duration = SnackbarDuration.Long
            )
            
            if (result == SnackbarResult.ActionPerformed) {
                onDismiss()
            }
        }
    }
}

// Validation utilities
object ValidationUtils {
    
    fun validateVehicleNickname(nickname: String): ValidationResult {
        return when {
            nickname.isBlank() -> ValidationResult.Error("Vehicle nickname is required")
            nickname.length < 2 -> ValidationResult.Error("Nickname must be at least 2 characters")
            nickname.length > 50 -> ValidationResult.Error("Nickname must be less than 50 characters")
            else -> ValidationResult.Success
        }
    }
    
    fun validateMake(make: String): ValidationResult {
        return when {
            make.isBlank() -> ValidationResult.Error("Make is required")
            make.length < 2 -> ValidationResult.Error("Make must be at least 2 characters")
            make.length > 30 -> ValidationResult.Error("Make must be less than 30 characters")
            else -> ValidationResult.Success
        }
    }
    
    fun validateModel(model: String): ValidationResult {
        return when {
            model.isBlank() -> ValidationResult.Error("Model is required")
            model.length < 2 -> ValidationResult.Error("Model must be at least 2 characters")
            model.length > 30 -> ValidationResult.Error("Model must be less than 30 characters")
            else -> ValidationResult.Success
        }
    }
    
    fun validateYear(year: Int): ValidationResult {
        return when {
            year < 1900 -> ValidationResult.Error("Year must be after 1900")
            year > (java.time.Year.now().value + 1) -> ValidationResult.Error("Year cannot be in the distant future")
            else -> ValidationResult.Success
        }
    }
    
    fun validateMileage(mileage: Int): ValidationResult {
        return when {
            mileage < 0 -> ValidationResult.Error("Mileage cannot be negative")
            mileage > 10000000 -> ValidationResult.Error("Mileage seems too high")
            else -> ValidationResult.Success
        }
    }
    
    fun validateCost(cost: Double): ValidationResult {
        return when {
            cost < 0 -> ValidationResult.Error("Cost cannot be negative")
            cost > 10000000 -> ValidationResult.Error("Cost seems too high")
            else -> ValidationResult.Success
        }
    }
    
    fun validateVIN(vin: String?): ValidationResult {
        if (vin.isNullOrBlank()) return ValidationResult.Success // VIN is optional
        
        return when {
            vin.length != 17 -> ValidationResult.Error("VIN must be exactly 17 characters")
            !vin.all { it.isLetterOrDigit() } -> ValidationResult.Error("VIN can only contain letters and numbers")
            else -> ValidationResult.Success
        }
    }
    
    fun validateLicensePlate(licensePlate: String?): ValidationResult {
        if (licensePlate.isNullOrBlank()) return ValidationResult.Success // License plate is optional
        
        return when {
            licensePlate.length > 15 -> ValidationResult.Error("License plate must be less than 15 characters")
            licensePlate.contains(Regex("[^a-zA-Z0-9\\-\\s]")) -> ValidationResult.Error("License plate contains invalid characters")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
    
    val isValid: Boolean
        get() = this is Success
}
