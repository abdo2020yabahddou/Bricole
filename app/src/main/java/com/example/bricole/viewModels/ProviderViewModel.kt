package com.example.bricole.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bricole.repository.ProviderRepository
import com.example.bricole.api.RetrofitInstance
import com.example.bricole.data.JoinRequest
import com.example.bricole.data.JoinState
import com.example.bricole.data.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProviderViewModel(private val providerRepository: ProviderRepository) : ViewModel() {

    private val _proSearchState = MutableStateFlow(SearchState())
    val proSearchState: StateFlow<SearchState> = _proSearchState.asStateFlow()

    private val _proJoinState = MutableStateFlow(JoinState())
    val proJoinState: StateFlow<JoinState> = _proJoinState.asStateFlow()

    init {
        loadCities()
    }

    fun search(serviceId: Int, city: String) {
        viewModelScope.launch {
            _proSearchState.value =
                _proSearchState.value.copy(isLoading = true, error = null, isSearching = true)
            val result = providerRepository.search(serviceId, city)
            result.onSuccess { providers ->
                _proSearchState.value = _proSearchState.value.copy(
                    providers = providers,
                    isLoading = false,
                    isSearching = false
                )
            }
            result.onFailure {
                _proSearchState.value = _proSearchState.value.copy(
                    isLoading = false,
                    error = "couldn't find anything, try again",
                    isSearching = false
                )
            }
        }
    }

    fun join(request: JoinRequest) {
        viewModelScope.launch {
            _proJoinState.value =
                _proJoinState.value.copy(
                    isLoading = true,
                    error = null,
                    isJoining = true,
                    joinSuccess = false
                )

            val result = providerRepository.join(request)
            result.onSuccess { providers ->
                _proJoinState.value = _proJoinState.value.copy(
                    isLoading = false,
                    providers = listOf(providers),
                    isJoining = false,
                    joinSuccess = true
                )
            }
            result.onFailure {
                _proJoinState.value = _proJoinState.value.copy(
                    isLoading = false,
                    isJoining = false,
                    joinSuccess = false,
                    error = "Invalid operation,try again!!"
                )
            }
        }
    }

    fun loadCities() {
        viewModelScope.launch {
            val result = providerRepository.getAllCities()
            result.onSuccess { cities ->
                _proSearchState.value = _proSearchState.value.copy(cities = cities)
            }
            result.onFailure { e ->
                _proSearchState.value = _proSearchState.value.copy(error = e.message)
            }
        }
    }

    fun clearSearch() {
        _proSearchState.value = _proSearchState.value.copy(isSearching = false)
    }

    fun clearJoin(){
        _proJoinState.value=_proJoinState.value.copy(joinSuccess = false)
    }


    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProviderViewModel(providerRepository = ProviderRepository(providerApi = RetrofitInstance.providerApi))
            }
        }
    }
}