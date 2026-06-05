package com.example.bricole.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//localhost api: http://192.168.11.103:8080/
//https://bricole-spring-boot-backend.onrender.com
private const val BASE_URL = "https://bricole-spring-boot-backend.onrender.com"

object RetrofitInstance {
    var MOCK_ENABLED = true

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(MockInterceptor(enabled = MOCK_ENABLED))
        .retryOnConnectionFailure(true)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val serviceApi: ServiceApi by lazy { retrofit.create(ServiceApi::class.java) }
    val providerApi: ProviderApi by lazy { retrofit.create(ProviderApi::class.java) }
}