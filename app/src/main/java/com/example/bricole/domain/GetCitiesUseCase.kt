package com.example.bricole.domain

import com.example.bricole.repository.ProviderRepository
import kotlin.collections.orEmpty

class GetCitiesUseCase(
    private val providerRepository: ProviderRepository,
) {
    suspend operator fun invoke(): List<String> {
        val result = providerRepository.getAllCities()
        if (result.isSuccess) {
            return result.getOrNull().orEmpty()
        }
        return emptyList()
    }
}