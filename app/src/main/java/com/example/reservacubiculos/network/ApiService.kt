package com.example.reservacubiculos.network

import com.example.reservacubiculos.model.Cubiculo
import com.example.reservacubiculos.model.Reserva
import com.example.reservacubiculos.model.ReservaRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Cada función es un endpoint de la API. La ruta se une con la URL base (ver RetrofitClient).
interface ApiService {

// Sprint 1: consultas (GET)

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

// Sprint 2: escritura

    // HU-05 y HU-06: crear una reserva (si el horario se cruza, la API responde 409)
    @POST("reservas")
    suspend fun crearReserva(@Body reserva: ReservaRequest): Response<Reserva>

    // HU-07: editar / reprogramar una reserva (también válida el cruce de horarios)
    @PUT("reservas/{id}")
    suspend fun actualizarReserva(
        @Path("id") id: String,
        @Body reserva: ReservaRequest
    ): Response<Reserva>

    // HU-08: cancelar (la API cambia el estado a CANCELADA, no borra el registro)
    @DELETE("reservas/{id}")
    suspend fun cancelarReserva(@Path("id") id: String): Response<Unit>
}
