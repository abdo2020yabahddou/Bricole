package com.example.bricole.repository

import android.util.Log
import com.example.bricole.api.ServiceApi
import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.data.ServiceResponseDto


class ServiceRepository(private val serviceApi: ServiceApi) {

    suspend fun getAllServices(): Result<List<ServiceResponseDto>> {
        return try {
            Result.success(serviceApi.getAllServices())
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())
            Result.failure(e)
        }
    }

    suspend fun getProviders(id: Int): Result<List<ProviderResponseDto>> {
        return try {
            Result.success(serviceApi.getProviders(id))
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())
            Result.failure(e)
        }

    }
}