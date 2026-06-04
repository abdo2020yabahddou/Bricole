package com.example.bricole.ui.screens.provider.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bricole.api.RetrofitInstance
import com.example.bricole.data.JoinRequest
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

class JoinViewModel(
    private val providerRepository: ProviderRepository,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getServicesUseCase: GetServicesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinState.default)
    val uiState: StateFlow<JoinState> = _uiState.asStateFlow()

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


    fun join(request: JoinRequest) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isJoining = true,
                    joinSuccess = false
                )
            }
            val result = providerRepository.join(request)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isJoining = false,
                        joinSuccess = true
                    )
                }
            }
            result.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isJoining = false,
                        joinSuccess = false,
                        error = "Invalid operation,try again!!"
                    )
                }
            }
        }
    }

    fun clearJoin() {
        _uiState.update {
            it.copy(joinSuccess = false)
        }
    }

    fun retryJoin(request: JoinRequest) {
        _uiState.update {
            it.copy(isLoading = true, retryCount = it.retryCount + 1)
        }
        join(request)
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
                JoinViewModel(
                    providerRepository = providerRepository,
                    getServicesUseCase = GetServicesUseCase(serviceRepository),
                    getCitiesUseCase = GetCitiesUseCase(providerRepository),
                )
            }
        }
    }
}