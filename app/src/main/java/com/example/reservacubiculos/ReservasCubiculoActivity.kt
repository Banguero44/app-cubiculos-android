package com.example.reservacubiculos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservacubiculos.adapter.ReservaAdapter
import com.example.reservacubiculos.databinding.ActivityReservasBinding
import com.example.reservacubiculos.model.Reserva
import com.example.reservacubiculos.network.RetrofitClient
import com.example.reservacubiculos.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Locale

// Pantalla 2 - Reservas de un cubículo por fecha (HU-02): consulta a la API con parámetros
class ReservasCubiculoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservasBinding
    private lateinit var cubiculoId: String
    private lateinit var cubiculoNombre: String
    private lateinit var cubiculoDetalle: String
    private var fechaConsulta = hoy() // por defecto, la fecha actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepararPantalla(binding.root)

        val id = intent.getStringExtra("cubiculo_id")
        if (id == null) {
            finish()
            return
        }
        cubiculoId = id
        cubiculoNombre = intent.getStringExtra("cubiculo_nombre") ?: "Cubículo"
        cubiculoDetalle = intent.getStringExtra("cubiculo_detalle") ?: ""

        binding.barra.tvTitulo.text = cubiculoNombre
        binding.barra.btnAtras.setOnClickListener { finish() }
        binding.tvCubiculoNombre.text = cubiculoNombre
        binding.tvCubiculoDetalle.text = cubiculoDetalle

        binding.recyclerReservas.layoutManager = LinearLayoutManager(this)
        binding.filaFecha.setOnClickListener { elegirFecha() }
        binding.fabNueva.setOnClickListener { abrirFormulario() }
    }

    // Al volver de otra pantalla se vuelve a consultar la API
    override fun onResume() {
        super.onResume()
        if (::cubiculoId.isInitialized) cargarReservas()
    }

    private fun cargarReservas() {
        binding.tvFecha.text = fechaLarga(fechaConsulta)
        binding.tvVacio.visibility = View.GONE
        binding.recyclerReservas.visibility = View.GONE
        binding.estado.mostrarCargando()

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.getReservas(cubiculoId, fechaConsulta)
                if (respuesta.isSuccessful) {
                    val lista = respuesta.body().orEmpty().sortedBy { it.horaInicio }
                    binding.estado.ocultar()
                    binding.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerReservas.adapter = ReservaAdapter(lista) { abrirDetalle(it) }
                    binding.recyclerReservas.visibility = View.VISIBLE
                } else {
                    val mensaje = mensajeDeError(respuesta.errorBody()?.string(), respuesta.code())
                    binding.estado.mostrarError(mensaje) { cargarReservas() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                binding.estado.mostrarError("No hay conexión con el servidor. Revisa tu internet e inténtalo de nuevo.") { cargarReservas() }
            } catch (e: Exception) {
                binding.estado.mostrarError("Ocurrió un error inesperado.") { cargarReservas() }
            }
        }
    }

    private fun abrirDetalle(reserva: Reserva) {
        val intent = Intent(this, DetalleReservaActivity::class.java)
        intent.putExtra("reserva_id", reserva._id)
        startActivity(intent)
    }

    private fun abrirFormulario() {
        val intent = Intent(this, FormularioReservaActivity::class.java)
        intent.putExtra("cubiculo_id", cubiculoId)
        intent.putExtra("cubiculo_nombre", cubiculoNombre)
        intent.putExtra("cubiculo_detalle", cubiculoDetalle)
        intent.putExtra("fecha", fechaConsulta)
        startActivity(intent)
    }

    private fun elegirFecha() {
        val p = fechaConsulta.split("-").map { it.toInt() }
        DatePickerDialog(this, { _, anio, mes, dia ->
            fechaConsulta = "%04d-%02d-%02d".format(Locale.US, anio, mes + 1, dia)
            cargarReservas()
        }, p[0], p[1] - 1, p[2]).show()
    }

    private fun hoy(): String {
        val c = Calendar.getInstance()
        return "%04d-%02d-%02d".format(Locale.US, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }
}
