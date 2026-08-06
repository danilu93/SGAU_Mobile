package com.example.proyectofinal_danielavega.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_danielavega.R
import com.example.proyectofinal_danielavega.models.Planta

// Adapter para mostrar la lista de plantas en un RecyclerView
class PlantaAdapter(
    private var plantas: List<Planta>,
    private val esConsulta: Boolean,
    private val onVerDetalleClick: (Planta) -> Unit,
    private val onEditarClick: (Planta) -> Unit,
    private val onEliminarClick: (Planta) -> Unit
) : RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder>() {

    // ViewHolder para cada elemento de la lista de plantas
    class PlantaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePlanta)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipoPlanta)
        val btnVerDetalle: ImageButton = itemView.findViewById(R.id.btnVerDetalle)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarPlanta)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarPlanta)
    }

    // Crea un nuevo ViewHolder para un elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planta, parent, false)
        return PlantaViewHolder(vista)
    }

    // Vincula los datos de una planta a un ViewHolder
    override fun onBindViewHolder(holder: PlantaViewHolder, position: Int) {
        val planta = plantas[position]

        holder.tvNombre.text = planta.nombrePlanta
        holder.tvTipo.text = planta.tipoPlanta.etiqueta

        holder.btnVerDetalle.setOnClickListener { onVerDetalleClick(planta) }

        // Oculta los botones de editar y eliminar si es una consulta
        if (esConsulta) {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        } else {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE

            holder.btnEditar.setOnClickListener { onEditarClick(planta) }
            holder.btnEliminar.setOnClickListener { onEliminarClick(planta) }
        }
    }

    // Devuelve el número de elementos en la lista de plantas
    override fun getItemCount(): Int = plantas.size

    // Actualiza la lista de plantas y notifica al adaptador que los datos han cambiado
    fun actualizarLista(nuevaLista: List<Planta>) {
        plantas = nuevaLista
        notifyDataSetChanged()
    }
}