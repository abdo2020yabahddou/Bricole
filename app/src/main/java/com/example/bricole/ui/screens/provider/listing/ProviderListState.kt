package com.example.bricole.ui.screens.provider.listing

import com.example.bricole.data.ProviderResponseDto

data class ProviderListState(
    val providers: List<ProviderResponseDto>,
    val isLoading: Boolean,
    val error: String?,
    val retryCount: Int
) {
    companion object {
        val loading = ProviderListState(
            providers = emptyList(),
            isLoading = true,
            error = null,
            retryCount = 0
        )
    }
}