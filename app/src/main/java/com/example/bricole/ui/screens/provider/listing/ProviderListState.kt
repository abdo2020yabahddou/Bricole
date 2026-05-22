package com.example.bricole.ui.screens.provider.listing

import com.example.bricole.data.ProviderResponseDto

data class ProviderListState(
    val providers: List<ProviderResponseDto>,
    val isLoading: Boolean,
    val isError: Boolean
) {
    companion object {
        val loading = ProviderListState(
            providers = emptyList(),
            isLoading = true,
            isError = false
        )
    }
}