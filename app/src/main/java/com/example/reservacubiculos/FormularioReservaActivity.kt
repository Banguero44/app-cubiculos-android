package com.example.reservacubiculos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reservacubiculos.databinding.ActivityFormularioBinding
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.reservacubiculos.model.ReservaRequest
import com.example.reservacubiculos.network.RetrofitClient
import com.example.reservacubiculos.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

// Pantalla 4 - Formulario para crear o editar una reserva (HU-05, HU-06, HU-07, HU-09)
class FormularioReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private lateinit var cubiculoId: String
    private var reservaId: String? = null // null = crear, con valor = editar
    private var fecha: String? = null
    private var horaInicio: String? = null
    private var horaFin: String? = null
    private var textoBoton = "Crear reserva"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepararPantalla(binding.root)

        val idCubiculo = intent.getStringExtra("cubiculo_id")
        if (idCubiculo == null) {
            finish()
            return
        }
        cubiculoId = idCubiculo
        reservaId = intent.getStringExtra("reserva_id")
        val editando = reservaId != null
        textoBoton = if (editando) "Guardar cambios" else "Crear reserva"

        binding.barra.tvTitulo.text = if (editando) "Editar reserva" else "Nueva reserva"
        binding.btnGuardar.text = textoBoton
        binding.tvCubiculoNombre.text = intent.getStringExtra("cubiculo_nombre") ?: "Cubículo"
        binding.tvCubiculoDetalle.text = intent.getStringExtra("cubiculo_detalle") ?: ""

        fecha = intent.getStringExtra("fecha")
        if (editando) {
            binding.etNombre.setText(intent.getStringExtra("nombre"))
            binding.etCodigo.setText(intent.getStringExtra("codigo"))
            horaInicio = intent.getStringExtra("hora_inicio")
            horaFin = intent.getStringExtra("hora_fin")
        }
        mostrarValores()

        binding.barra.btnAtras.setOnClickListener { finish() }
        binding.tvFecha.setOnClickListener { elegirFecha() }
        binding.tvHoraInicio.setOnClickListener { elegirHora(true) }
        binding.tvHoraFin.setOnClickListener { elegirHora(false) }
        binding.btnGuardar.setOnClickListener { guardar() }
    }

    private fun mostrarValores() {
        binding.tvFecha.text = fecha?.let { fechaNumerica(it) } ?: "Elige la fecha"
        binding.tvHoraInicio.text = horaInicio ?: "Elige la hora"
        binding.tvHoraFin.text = horaFin ?: "Elige la hora"
    }

    private fun elegirFecha() {
        val p = (fecha ?: "2026-11-10").split("-").map { it.toInt() }
        DatePickerDialog(this, { _, anio, mes, dia ->
            fecha = "%04d-%02d-%02d".format(Locale.US, anio, mes + 1, dia)
            mostrarValores()
        }, p[0], p[1] - 1, p[2]).show()
    }

    private fun elegirHora(esInicio: Boolean) {
        val actual = (if (esInicio) horaInicio else horaFin) ?: "10:00"
        val partes = actual.split(":").map { it.toInt() }
        TimePickerDialog(this, { _, hora, minuto ->
            val texto = "%02d:%02d".format(Locale.US, hora, minuto)
            if (esInicio) horaInicio = texto else horaFin = texto
            mostrarValores()
        }, partes[0], partes[1], true).show()
    }

    private fun guardar() {
        val nombre = binding.etNombre.text.toString().trim()
        val codigo = binding.etCodigo.text.toString().trim()
        val f = fecha
        val hi = horaInicio
        val hf = horaFin

        // Validación sencilla en la app, la validación real (incluida la regla de "no cruzar horarios") la hace la API y la app solo muestra su respuesta.
        if (nombre.isEmpty() || codigo.isEmpty() || f == null || hi == null || hf == null) {
            mensaje("Completa todos los campos")
            return
        }
        if (hi >= hf) {
            mensaje("La hora de inicio debe ser menor que la hora de fin")
            return
        }
        enviar(ReservaRequest(cubiculoId, nombre, codigo, f, hi, hf))
    }

    // Crea (POST) o edita (PUT) la reserva en la API
    private fun enviar(datos: ReservaRequest) {
        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.text = "Guardando…"
        val id = reservaId

        lifecycleScope.launch {
            try {
                val respuesta = if (id == null) {
                    RetrofitClient.api.crearReserva(datos)
                } else {
                    RetrofitClient.api.actualizarReserva(id, datos)
                }
                if (respuesta.isSuccessful) {
                    mensaje(if (id == null) "Reserva creada correctamente" else "Cambios guardados correctamente")
                    finish()
                } else {
                    // 400 datos inválidos, 404 no existe, 409 horario ocupado: la API explica al usuario
                    avisarError(respuesta.code(), mensajeDeError(respuesta.errorBody()?.string(), respuesta.code()))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                avisarError(0, "No hay conexión con el servidor. Revisa tu internet e inténtalo de nuevo.")
            } catch (e: Exception) {
                avisarError(0, "Ocurrió un error inesperado.")
            }
        }
    }

    private fun avisarError(codigo: Int, texto: String) {
        binding.btnGuardar.isEnabled = true
        binding.btnGuardar.text = textoBoton
        val titulo = when (codigo) {
            409 -> "Horario no disponible"
            400 -> "Revisa los datos"
            404 -> "No se encontró"
            else -> "No se pudo guardar"
        }
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(texto)
            .setPositiveButton("Entendido", null)
            .show()
    }
}
