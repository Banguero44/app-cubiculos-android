package com.example.reservacubiculos

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reservacubiculos.databinding.EstadoCargaBinding
import java.text.SimpleDateFormat
import java.util.Locale

// Deja la pantalla "de borde a borde" y respeta las barras del sistema (estado y navegación)
fun AppCompatActivity.prepararPantalla(raiz: View) {
    enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
    ViewCompat.setOnApplyWindowInsetsListener(raiz) { vista, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
        insets
    }
}

fun Context.mensaje(texto: String) {
    Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
}

private val ES_CO: Locale = Locale.forLanguageTag("es-CO")

// 2026-10-08 -> jueves, 8 de octubre de 2026
fun fechaLarga(fecha: String): String {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha) ?: return fecha
    return SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", ES_CO).format(d).replaceFirstChar { it.uppercase() }
}

// 2026-10-08 -> 8 oct 2026
fun fechaCorta(fecha: String): String {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha) ?: return fecha
    return SimpleDateFormat("d MMM yyyy", ES_CO).format(d)
}

// 2026-10-08 -> 08 / 10 / 2026
fun fechaNumerica(fecha: String): String {
    val p = fecha.split("-")
    return "${p[2]} / ${p[1]} / ${p[0]}"
}

// Estado de carga y de error (layout estado_carga.xml)
fun EstadoCargaBinding.mostrarCargando(texto: String = "Conectando con el servidor…\nLa primera vez puede tardar hasta 1 minuto.") {
    root.visibility = View.VISIBLE
    progreso.visibility = View.VISIBLE
    btnReintentar.visibility = View.GONE
    tvEstadoCarga.text = texto
}

fun EstadoCargaBinding.mostrarError(texto: String, reintentar: () -> Unit) {
    root.visibility = View.VISIBLE
    progreso.visibility = View.GONE
    tvEstadoCarga.text = texto
    btnReintentar.visibility = View.VISIBLE
    btnReintentar.setOnClickListener { reintentar() }
}

fun EstadoCargaBinding.ocultar() {
    root.visibility = View.GONE
}
