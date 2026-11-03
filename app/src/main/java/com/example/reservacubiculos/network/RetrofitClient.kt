package com.example.reservacubiculos.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL base de la API, siempre debe terminar en "/":
    const val BASE_URL = "https://backend-cubiculos.onrender.com/api/"

    // Gestion de tiempos largos porque el servidor gratuito de la nube render puede tardar en "despertar"
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create()) // convierte el JSON en las data class
            .build()
            .create(ApiService::class.java)
    }
}
