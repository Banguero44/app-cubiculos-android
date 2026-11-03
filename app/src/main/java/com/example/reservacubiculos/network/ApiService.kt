package com.example.reservacubiculos.network

import com.example.reservacubiculos.model.Cubiculo
import com.example.reservacubiculos.model.Reserva
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Cada función es un endpoint de la API. La ruta se une con la URL base (ver RetrofitClient).
interface ApiService {

    // HU-01: lista de cubículos
    @GET("cubiculos")
    suspend fun getCubiculos(): Response<List<Cubiculo>>

    // HU-02: reservas de un cubículo en una fecha (consulta con parámetros)
    @GET("reservas")
    suspend fun getReservas(
        @Query("cubiculoId") cubiculoId: String,
        @Query("fecha") fecha: String
    ): Response<List<Reserva>>

    // HU-03: detalle de una reserva
    @GET("reservas/{id}")
    suspend fun getReserva(@Path("id") id: String): Response<Reserva>

    // Sprint 2: crear (POST), editar (PUT) y cancelar (DELETE) reservas (en el futuro)
}
