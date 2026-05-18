package com.example.bricole.data

import com.google.gson.annotations.SerializedName


data class ServiceResponseDto(
    val id: Int,
    val name: String,
    val icon: String?
)

data class ProviderResponseDto(
    val proId: Int,
    @SerializedName("proName") val name: String,
    val phone: String,
    val city: String?,
    val serviceId: Int,
    val serviceName: String
)

data class JoinRequest(
    val name: String,
    val phone: String,
    val city: String,
    val serviceId: Int?
)
