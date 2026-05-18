package com.example.bricole.api

import com.example.bricole.data.JoinRequest
import com.example.bricole.data.ProviderResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProviderApi {
    @GET("providers/search")
    suspend fun search(
        @Query("serviceId") serviceId: Int,
        @Query("city") city: String
    ): List<ProviderResponseDto>

    @POST("providers/join")
    suspend fun join(@Body request: JoinRequest): ProviderResponseDto

    @GET("providers/cities")
    suspend fun getAllCities(): List<String>
}