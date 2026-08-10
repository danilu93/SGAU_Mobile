package com.example.proyectofinal_danielavega

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.utils.ToastPersonalizado

// Pantalla que muestra los detalles de una planta
class DetallePlantaActivity : AppCompatActivity() {

    // Acceso a la base de datos
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_planta)

        // Conexión a la base de datos
        dbHelper = DbHelper(this)

        // Se obtienen los elementos de la pantalla
        val tvNombre = findViewById<TextView>(R.id.tvDetalleNombre)
        val tvTipo = findViewById<TextView>(R.id.tvDetalleTipo)
        val tvNombreCientifico = findViewById<TextView>(R.id.tvDetalleNombreCientifico)
        val tvFechaSiembra = findViewById<TextView>(R.id.tvDetalleFechaSiembra)
        val tvMetodoSiembra = findViewById<TextView>(R.id.tvDetalleMetodoSiembra)
        val tvFechaRegistro = findViewById<TextView>(R.id.tvDetalleFechaRegistro)
        val tvObservaciones = findViewById<TextView>(R.id.tvDetalleObservaciones)
        val btnCerrar = findViewById<Button>(R.id.btnCerrarDetalle)

        // Casilla para mostrar u ocultar la información adicional
        val cbMostrarAdicional = findViewById<android.widget.CheckBox>(R.id.cbMostrarAdicional)
        val contenedorAdicional = findViewById<android.widget.LinearLayout>(R.id.contenedorAdicional)

        cbMostrarAdicional.setOnCheckedChangeListener { _, marcado ->
            contenedorAdicional.visibility = if (marcado) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Se recibe el id de la planta desde la pantalla anterior
        val plantaId = intent.getIntExtra("plantaId", -1)

        try {
            // Se busca la planta en la base de datos
            val planta = if (plantaId != -1) dbHelper.obtenerPlantaPorId(plantaId) else null

            // Si la planta existe se muestran sus datos en pantalla
            if (planta != null) {
                tvNombre.text = planta.nombrePlanta
                tvTipo.text = planta.tipoPlanta.etiqueta
                tvNombreCientifico.text = planta.nombreCientifico ?: "No especificado"
                tvFechaSiembra.text = planta.fechaSiembra
                tvMetodoSiembra.text = planta.metodoSiembra ?: "No especificado"
                tvFechaRegistro.text = planta.fechaRegistro ?: "No especificado"
                tvObservaciones.text = planta.observaciones ?: "Sin observaciones"
            } else {
                // Si no se encuentra, se avisa y se cierra la pantalla
                ToastPersonalizado.mostrarToast(this, "No se encontro la planta")
                finish()
            }
        } catch (e: Exception) {
            ToastPersonalizado.mostrarToast(this, "Error al cargar el detalle")
            e.printStackTrace()
            finish()
        }

        // Botón para cerrar la pantalla
        btnCerrar.setOnClickListener {
            finish()
        }
    }
}
