package com.example.bricole.ui.screens.provider.listing

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

class ProviderListViewModel(
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProviderListState.loading)
    val uiState: StateFlow<ProviderListState> = _uiState.asStateFlow()

    fun loadProviders(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, isError = false)
            }
            val result = serviceRepository.getProviders(id)
            result.onSuccess { providers ->
                _uiState.update {
                    it.copy(providers = providers, isLoading = false)
                }
            }
            result.onFailure {
                _uiState.update {
                    it.copy(isLoading = false, isError = true)
                }
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val serviceRepository = ServiceRepository(
                    serviceApi = RetrofitInstance.serviceApi
                )
                ProviderListViewModel(serviceRepository = serviceRepository)
            }
        }
    }
}


