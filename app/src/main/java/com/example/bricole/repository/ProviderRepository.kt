package com.example.bricole.repository

import android.util.Log
import com.example.bricole.data.JoinRequest
import com.example.bricole.api.ProviderApi
import com.example.bricole.data.ProviderResponseDto
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response

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

    // Just an example of custom error handling
    suspend fun join2(request: JoinRequest): JoinResult {
        return try {
            JoinResult.Success(providerApi.join(request))
        } catch (e: Exception) {
            Log.e("API", e.stackTraceToString())

            if (e is HttpException) {
                val errorMessage = e.response()?.errorBody()?.string()
                val serverError = Gson().fromJson(
                    errorMessage.orEmpty(), ServerErrorResponse::class.java
                )

                if (serverError.code == "error_invalid_phone_number") {
                    return JoinResult.InvalidPhoneNumber
                }
            }

            return JoinResult.Error
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


// Just an example of custom error handling
sealed interface JoinResult {
    data class Success(val response: ProviderResponseDto) : JoinResult
    data object InvalidPhoneNumber : JoinResult
    data object Error : JoinResult
}

val message =
    "{\n  \"code\": \"error_invalid_phone_number\",\n  \"message\": \"The phone number format is invalid\"\n}"

data class ServerErrorResponse(
    val code: String?,
    val message: String?
)