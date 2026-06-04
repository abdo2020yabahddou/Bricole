package com.example.bricole.ui.screens.provider.search

import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.data.ServiceResponseDto

data class SearchState(
    val services: List<ServiceResponseDto>,
    val providers: List<ProviderResponseDto>,
    val cities: List<String>,
    val isLoading: Boolean,
    val isSearching: Boolean,
    val error: String?,
    val retryCount: Int
) {
    companion object {
        val default = SearchState(
            services = emptyList(),
            providers = emptyList(),
            cities = emptyList(),
            isLoading = false,
            isSearching = false,
            error = null,
            retryCount = 0
        )
    }
}