package com.example.bricole.domain

import com.example.bricole.data.ServiceResponseDto
import com.example.bricole.repository.ServiceRepository

class GetServicesUseCase(
    private val serviceRepository: ServiceRepository,
) {
    suspend operator fun invoke(): List<ServiceResponseDto> {
        val result = serviceRepository.getAllServices()
        if (result.isSuccess) {
            return result.getOrNull().orEmpty()
        }
        return emptyList()
    }
}