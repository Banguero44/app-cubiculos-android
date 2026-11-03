package com.example.reservacubiculos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.reservacubiculos.databinding.ActivityDetalleBinding
import com.example.reservacubiculos.model.Reserva
import com.example.reservacubiculos.network.RetrofitClient
import com.example.reservacubiculos.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

// Pantalla 3 - Detalle de la reserva (HU-03): los datos vienen de la API
class DetalleReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleBinding
    private lateinit var reservaId: String
    private var reserva: Reserva? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepararPantalla(binding.root)

        val id = intent.getStringExtra("reserva_id")
        if (id == null) {
            finish()
            return
        }
        reservaId = id

        binding.barra.tvTitulo.text = "Detalle de la reserva"
        binding.barra.btnAtras.setOnClickListener { finish() }
        binding.btnEditar.setOnClickListener { abrirFormulario() }
        binding.btnCancelar.setOnClickListener { confirmarCancelacion() }
    }

    override fun onResume() {
        super.onResume()
        if (::reservaId.isInitialized) cargarReserva()
    }

    private fun cargarReserva() {
        binding.contenido.visibility = View.GONE
        binding.estado.mostrarCargando()

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.getReserva(reservaId)
                val datos = respuesta.body()
                if (respuesta.isSuccessful && datos != null) {
                    reserva = datos
                    mostrar(datos)
                    binding.estado.ocultar()
                    binding.contenido.visibility = View.VISIBLE
                } else {
                    val mensaje = mensajeDeError(respuesta.errorBody()?.string(), respuesta.code())
                    binding.estado.mostrarError(mensaje) { cargarReserva() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                binding.estado.mostrarError("No hay conexión con el servidor. Revisa tu internet e inténtalo de nuevo.") { cargarReserva() }
            } catch (e: Exception) {
                binding.estado.mostrarError("Ocurrió un error inesperado.") { cargarReserva() }
            }
        }
    }

    private fun mostrar(r: Reserva) {
        val activa = r.estado == "ACTIVA"
        binding.tvNombre.text = r.nombreEstudiante
        binding.tvCodigo.text = r.codigoEstudiante
        binding.tvSesion.text = "${fechaCorta(r.fecha)} · ${r.horaInicio}-${r.horaFin}"
        binding.tvEstado.text = if (activa) "Activa" else "Cancelada"
        binding.tvEstado.setBackgroundResource(if (activa) R.drawable.bg_chip_activa else R.drawable.bg_chip_cancelada)
        binding.tvEstado.setTextColor(ContextCompat.getColor(this, if (activa) R.color.azul else R.color.rojo))

        // Una reserva cancelada ya no se puede editar ni cancelar otra vez (igual que en la API)
        binding.btnEditar.isEnabled = activa
        binding.btnCancelar.isEnabled = activa
        binding.btnEditar.alpha = if (activa) 1f else 0.4f
        binding.btnCancelar.alpha = if (activa) 1f else 0.4f
    }

    private fun abrirFormulario() {
        val r = reserva ?: return
        val intent = Intent(this, FormularioReservaActivity::class.java)
        intent.putExtra("reserva_id", r._id)
        intent.putExtra("cubiculo_id", r.cubiculo._id)
        intent.putExtra("cubiculo_nombre", r.cubiculo.nombre)
        intent.putExtra("cubiculo_detalle", listOfNotNull(r.cubiculo.capacidad?.let { "$it personas" }, r.cubiculo.ubicacion).joinToString(" · "))
        intent.putExtra("nombre", r.nombreEstudiante)
        intent.putExtra("codigo", r.codigoEstudiante)
        intent.putExtra("fecha", r.fecha)
        intent.putExtra("hora_inicio", r.horaInicio)
        intent.putExtra("hora_fin", r.horaFin)
        startActivity(intent)
    }

    private fun confirmarCancelacion() {
        AlertDialog.Builder(this)
            .setTitle("¿Cancelar esta reserva?")
            .setMessage("La reserva quedará en estado Cancelada y el horario volverá a estar libre.")
            .setNegativeButton("Mantener activa", null)
            .setPositiveButton("Sí, cancelar reserva") { _, _ ->
                mensaje("Cancelar la reserva se conecta a la API en el Sprint 2.")
            }
            .show()
    }
}
