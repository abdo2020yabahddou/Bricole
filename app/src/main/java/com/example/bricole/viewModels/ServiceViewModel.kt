package com.example.bricole.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bricole.api.RetrofitInstance
import com.example.bricole.data.HomeState
import com.example.bricole.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(private val serviceRepository: ServiceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    //to help survive configuration changes
    init {
        showServices()
    }

    //show our services
    fun showServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = serviceRepository.getAllServices()
            result.onSuccess { services ->
                _uiState.value = _uiState.value.copy(services = services, isLoading = false)
            }
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message
                )
            }
        }
    }

    //show our providers
    fun showProviders(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = serviceRepository.getProviders(id)
            result.onSuccess { providers ->
                _uiState.value = _uiState.value.copy(
                    providers = providers, isLoading = false
                )
            }
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message
                )
            }
        }
    }

    fun clearServices() {
        _uiState.value = _uiState.value.copy(services = emptyList())
    }

    fun clearProviders() {
        _uiState.value = _uiState.value.copy(providers = emptyList())
    }

    fun retryServices() {
        _uiState.value =
            _uiState.value.copy(isLoading = true, retryCount = _uiState.value.retryCount + 1)
        showServices()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ServiceViewModel(serviceRepository = ServiceRepository(serviceApi = RetrofitInstance.serviceApi))
            }
        }
    }
}

