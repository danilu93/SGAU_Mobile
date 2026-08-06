package com.example.proyectofinal_danielavega

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal_danielavega.data.DbHelper

class DetallePlantaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_planta)

        dbHelper = DbHelper(this)

        val tvNombre = findViewById<TextView>(R.id.tvDetalleNombre)
        val tvTipo = findViewById<TextView>(R.id.tvDetalleTipo)
        val tvNombreCientifico = findViewById<TextView>(R.id.tvDetalleNombreCientifico)
        val tvFechaSiembra = findViewById<TextView>(R.id.tvDetalleFechaSiembra)
        val tvMetodoSiembra = findViewById<TextView>(R.id.tvDetalleMetodoSiembra)
        val tvFechaRegistro = findViewById<TextView>(R.id.tvDetalleFechaRegistro)
        val tvObservaciones = findViewById<TextView>(R.id.tvDetalleObservaciones)
        val btnCerrar = findViewById<Button>(R.id.btnCerrarDetalle)

        val plantaId = intent.getIntExtra("plantaId", -1)

        try {
            val planta = if (plantaId != -1) dbHelper.obtenerPlantaPorId(plantaId) else null

            if (planta != null) {
                tvNombre.text = planta.nombrePlanta
                tvTipo.text = planta.tipoPlanta.etiqueta
                tvNombreCientifico.text = planta.nombreCientifico ?: "No especificado"
                tvFechaSiembra.text = planta.fechaSiembra
                tvMetodoSiembra.text = planta.metodoSiembra ?: "No especificado"
                tvFechaRegistro.text = planta.fechaRegistro ?: "No especificado"
                tvObservaciones.text = planta.observaciones ?: "Sin observaciones"
            } else {
                Toast.makeText(this, "No se encontró la planta", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar el detalle", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            finish()
        }

        btnCerrar.setOnClickListener {
            finish()
        }
    }
}