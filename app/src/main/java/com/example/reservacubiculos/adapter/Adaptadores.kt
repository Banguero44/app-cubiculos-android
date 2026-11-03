package com.example.reservacubiculos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reservacubiculos.R
import com.example.reservacubiculos.databinding.ItemCubiculoBinding
import com.example.reservacubiculos.databinding.ItemReservaBinding
import com.example.reservacubiculos.model.Cubiculo
import com.example.reservacubiculos.model.Reserva

// Lista de cubículos (pantalla 1)
class CubiculoAdapter(
    private val cubiculos: List<Cubiculo>,
    private val alTocar: (Cubiculo) -> Unit
) : RecyclerView.Adapter<CubiculoAdapter.VH>() {

    inner class VH(val b: ItemCubiculoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCubiculoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val c = cubiculos[position]
        h.b.tvNombre.text = c.nombre
        h.b.tvDetalle.text = "${c.capacidad} personas · ${c.ubicacion}"
        h.b.root.setOnClickListener { alTocar(c) }
    }

    override fun getItemCount() = cubiculos.size
}

// Lista de reservas de un cubículo (pantalla 2)
class ReservaAdapter(
    private val reservas: List<Reserva>,
    private val alTocar: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.VH>() {

    inner class VH(val b: ItemReservaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemReservaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val r = reservas[position]
        val color = if (r.estado == "ACTIVA") R.color.azul else R.color.gris_texto
        h.b.tvInicio.text = r.horaInicio
        h.b.tvInicio.setTextColor(ContextCompat.getColor(h.b.root.context, color))
        h.b.tvFin.text = r.horaFin
        h.b.tvEstudiante.text = r.nombreEstudiante
        h.b.root.setOnClickListener { alTocar(r) }
    }

    override fun getItemCount() = reservas.size
}
