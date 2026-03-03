package com.chiefdenis.carsart.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiefdenis.carsart.data.repository.ServiceRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServiceRecordViewModel @Inject constructor(
    private val serviceRecordRepository: ServiceRecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val serviceRecordId: UUID = UUID.fromString(savedStateHandle.get<String>("serviceRecordId")!!)

    private val _serviceRecord = MutableStateFlow<com.chiefdenis.carsart.data.database.ServiceRecord?>(null)
    val serviceRecord = _serviceRecord.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadServiceRecord() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _serviceRecord.value = serviceRecordRepository.getServiceRecordById(serviceRecordId)
            } catch (e: Exception) {
                // TODO: Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteServiceRecord() {
        viewModelScope.launch {
            try {
                serviceRecordRepository.deleteServiceRecordById(serviceRecordId)
                _serviceRecord.value = null
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}
