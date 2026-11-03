package com.example.reservacubiculos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reservacubiculos.databinding.ActivityFormularioBinding
import java.util.Locale

// Pantalla 4 - Formulario para crear o editar una reserva
// (Sprint 1: solo válida y muestra el formulario; guardar en la API lo realizaré en el Sprint 2)
class FormularioReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private var reservaId: String? = null // null = crear, con valor = editar
    private var fecha: String? = null
    private var horaInicio: String? = null
    private var horaFin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepararPantalla(binding.root)

        reservaId = intent.getStringExtra("reserva_id")
        val editando = reservaId != null

        binding.barra.tvTitulo.text = if (editando) "Editar reserva" else "Nueva reserva"
        binding.btnGuardar.text = if (editando) "Guardar cambios" else "Crear reserva"
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
        val p = (fecha ?: "2026-11-03").split("-").map { it.toInt() }
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
        val hi = horaInicio
        val hf = horaFin

        // Validación sencilla en la app, la validación real (incluida la regla de
        // "no cruzar horarios", que responde 409) la hace el backend.
        if (nombre.isEmpty() || codigo.isEmpty() || fecha == null || hi == null || hf == null) {
            mensaje("Completa todos los campos")
            return
        }
        if (hi >= hf) {
            mensaje("La hora de inicio debe ser menor que la hora de fin")
            return
        }
        mensaje("Datos válidos. Guardar en la API lo realizare en el Sprint 2.")
    }
}
