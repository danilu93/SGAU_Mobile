package com.example.proyectofinal_danielavega.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.proyectofinal_danielavega.R

// Objeto con la configuración de los avisos personalizados en pantalla
object ToastPersonalizado {

    // Muestra un mensaje corto con un diseño propio
    fun mostrarToast(context: Context, mensaje: String) {
        // Se carga el diseño del toast personalizado
        val layoutInflater = LayoutInflater.from(context)
        val vista = layoutInflater.inflate(R.layout.custom_toast, null)

        // Se escribe el mensaje en el texto del toast
        val tvMensaje = vista.findViewById<TextView>(R.id.tvMensajeToast)
        tvMensaje.text = mensaje

        // Se crea el toast con el diseño y se muestra
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = vista
        toast.show()
    }
}
