package com.example.reservacubiculos.model

// Estos modelos usan los mismos nombres de campos que devuelve la API (backend en MongoDB), por eso el identificador se llama _id.

data class Cubiculo(
    val _id: String,
    val nombre: String,
    val capacidad: Int,
    val ubicacion: String,
    val activo: Boolean = true
)

// Versión corta del cubículo que viene dentro de cada reserva (la API la entrega con "populate")
data class CubiculoResumen(
    val _id: String,
    val nombre: String,
    val ubicacion: String? = null,
    val capacidad: Int? = null
)

data class Reserva(
    val _id: String,
    val cubiculo: CubiculoResumen,
    val nombreEstudiante: String,
    val codigoEstudiante: String,
    val fecha: String,       // AAAA-MM-DD
    val horaInicio: String,  // HH:MM (24 horas)
    val horaFin: String,     // HH:MM (24 horas)
    val estado: String       // "ACTIVA" o "CANCELADA"
)

// Se enviará a la API al crear o editar una reserva (usado al conectar la API)
data class ReservaRequest(
    val cubiculoId: String,
    val nombreEstudiante: String,
    val codigoEstudiante: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String
)

// Forma del error que responde la API: { "error": "mensaje" }
data class ErrorResponse(val error: String)
