package com.example.bricole.data

data class HomeState(
    val services: List<ServiceResponseDto> = emptyList(),
    val providers: List<ProviderResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val retryCount: Int = 0
)

data class SearchState(
    val providers: List<ProviderResponseDto> = emptyList(),
    val cities: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)

data class JoinState(
    val providers: List<ProviderResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val isJoining: Boolean = false,
    val joinSuccess: Boolean = false,
    val error: String? = null
)
