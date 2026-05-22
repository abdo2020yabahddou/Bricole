package com.example.bricole.ui.screens.provider.join

import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.data.ServiceResponseDto

data class JoinState(
    val services: List<ServiceResponseDto>,
    val cities: List<String>,
    val isLoading: Boolean,
    val isJoining: Boolean,
    val joinSuccess: Boolean,
    val error: String?
) {
    companion object {
        val default = JoinState(
            services = emptyList(),
            cities = emptyList(),
            isLoading = false,
            isJoining = false,
            joinSuccess = false,
            error = null
        )
    }
}