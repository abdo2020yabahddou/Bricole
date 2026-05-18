package com.example.bricole.repository

import android.util.Log
import com.example.bricole.data.JoinRequest
import com.example.bricole.api.ProviderApi
import com.example.bricole.data.ProviderResponseDto

class ProviderRepository(private val providerApi: ProviderApi) {

    suspend fun search(serviceId: Int, city: String): Result<List<ProviderResponseDto>> {
        return try {
            Result.success(providerApi.search(serviceId, city))
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())
            Result.failure(e)
        }

    }

    suspend fun join(request: JoinRequest): Result<ProviderResponseDto> {
        return try {
            Result.success(providerApi.join(request))
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun getAllCities(): Result<List<String>> {
        return try {
            Result.success(providerApi.getAllCities())
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())
            Result.failure(e)
        }
    }
}