package com.example.bricole.api

import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.data.ServiceResponseDto
import retrofit2.http.GET
import retrofit2.http.Path


interface ServiceApi {
    @GET("services")
    suspend fun getAllServices(): List<ServiceResponseDto>

    @GET("services/providers/{id}")
    suspend fun getProviders(@Path("id") id: Int): List<ProviderResponseDto>
}