package com.example.reservacubiculos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservacubiculos.adapter.CubiculoAdapter
import com.example.reservacubiculos.databinding.ActivityMainBinding
import com.example.reservacubiculos.model.Cubiculo
import com.example.reservacubiculos.network.RetrofitClient
import com.example.reservacubiculos.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

// Pantalla 1 - Lista de cubículos (HU-01): los datos vienen de la API en la nub
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepararPantalla(binding.root)

        binding.barra.tvTitulo.text = "Cubículos - Biblioteca"
        binding.barra.btnAtras.setImageResource(R.drawable.ic_menu_book)
        binding.barra.btnAtras.isClickable = false
        binding.recyclerCubiculos.layoutManager = LinearLayoutManager(this)

        cargarCubiculos()
    }

    private fun cargarCubiculos() {
        binding.recyclerCubiculos.visibility = View.GONE
        binding.estado.mostrarCargando()

        /* La llamada de red se hace en una corrutina para no bloquear la pantalla */
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.getCubiculos()
                if (respuesta.isSuccessful) {
                    val lista = respuesta.body().orEmpty()
                    if (lista.isEmpty()) {
                        binding.estado.mostrarError("Todavía no hay cubículos registrados.") { cargarCubiculos() }
                    } else {
                        binding.recyclerCubiculos.adapter = CubiculoAdapter(lista) { abrirReservas(it) }
                        binding.recyclerCubiculos.visibility = View.VISIBLE
                        binding.estado.ocultar()
                    }
                } else {
                    val mensaje = mensajeDeError(respuesta.errorBody()?.string(), respuesta.code())
                    binding.estado.mostrarError(mensaje) { cargarCubiculos() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                binding.estado.mostrarError("No hay conexión con el servidor. Revisa tu internet e inténtalo de nuevo.") { cargarCubiculos() }
            } catch (e: Exception) {
                binding.estado.mostrarError("Ocurrió un error inesperado.") { cargarCubiculos() }
            }
        }
    }

    private fun abrirReservas(cubiculo: Cubiculo) {
        val intent = Intent(this, ReservasCubiculoActivity::class.java)
        intent.putExtra("cubiculo_id", cubiculo._id)
        intent.putExtra("cubiculo_nombre", cubiculo.nombre)
        intent.putExtra("cubiculo_detalle", "${cubiculo.capacidad} personas · ${cubiculo.ubicacion}")
        startActivity(intent)
    }
}
