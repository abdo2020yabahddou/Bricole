package com.example.bricole.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bricole.api.RetrofitInstance
import com.example.bricole.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState.loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }
            val result = serviceRepository.getAllServices()
            result.onSuccess { services ->
                _uiState.update {
                    it.copy(services = services, isLoading = false)
                }
            }
            result.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun retryServices() {
        _uiState.update {
            it.copy(isLoading = true, retryCount = it.retryCount + 1)
        }
        loadServices()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val serviceRepository = ServiceRepository(
                    serviceApi = RetrofitInstance.serviceApi
                )
                HomeViewModel(serviceRepository = serviceRepository)
            }
        }
    }
}