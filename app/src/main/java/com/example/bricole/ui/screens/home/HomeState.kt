package com.example.bricole.ui.screens.home

import com.example.bricole.data.ServiceResponseDto
import kotlin.collections.List

data class HomeState(
    val services: List<ServiceResponseDto>,
    val isLoading: Boolean,
    val error: String?,
    val retryCount: Int
) {
    companion object {
        val loading = HomeState(
            services = emptyList(),
            isLoading = true,
            error = null,
            retryCount = 0
        )
    }
}