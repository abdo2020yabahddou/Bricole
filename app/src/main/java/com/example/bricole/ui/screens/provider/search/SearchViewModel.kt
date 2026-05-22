package com.example.bricole.ui.screens.provider.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bricole.api.RetrofitInstance
import com.example.bricole.domain.GetCitiesUseCase
import com.example.bricole.domain.GetServicesUseCase
import com.example.bricole.repository.ProviderRepository
import com.example.bricole.repository.ServiceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val providerRepository: ProviderRepository,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getServicesUseCase: GetServicesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState.default)
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    init {
        prepareUiData()
    }

    fun prepareUiData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val citiesCall = async { getCitiesUseCase() }
        val servicesCall = async { getServicesUseCase() }

        val cities = citiesCall.await()
        val services = servicesCall.await()

        _uiState.update {
            it.copy(
                isLoading = false,
                services = services,
                cities = cities,
                error = if (cities.isEmpty() || services.isEmpty()) "Error" else null
            )
        }
    }

    fun search(serviceId: Int, city: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true, error = null, isSearching = true
            )
            val result = providerRepository.search(serviceId, city)
            result.onSuccess { providers ->
                _uiState.update {
                    it.copy(
                        providers = providers,
                        isLoading = false,
                        isSearching = false
                    )
                }
            }
            result.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "couldn't find anything, try again",
                        isSearching = false
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(isSearching = false)
    }


    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val providerRepository = ProviderRepository(
                    providerApi = RetrofitInstance.providerApi
                )
                val serviceRepository = ServiceRepository(
                    serviceApi = RetrofitInstance.serviceApi
                )
                SearchViewModel(
                    providerRepository = providerRepository,
                    getServicesUseCase = GetServicesUseCase(serviceRepository),
                    getCitiesUseCase = GetCitiesUseCase(providerRepository),
                )
            }
        }
    }
}