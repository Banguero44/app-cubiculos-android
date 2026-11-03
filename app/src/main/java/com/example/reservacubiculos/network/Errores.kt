package com.example.reservacubiculos.network

import com.example.reservacubiculos.model.ErrorResponse
import com.google.gson.Gson

// La API responde los errores como { "error": "mensaje" }, de aquí sale para mostrarlo.
fun mensajeDeError(cuerpo: String?, codigo: Int): String {
    val texto = try {
        Gson().fromJson(cuerpo, ErrorResponse::class.java)?.error
    } catch (e: Exception) {
        null
    }
    return texto ?: "El servidor respondió con un error ($codigo)."
}
