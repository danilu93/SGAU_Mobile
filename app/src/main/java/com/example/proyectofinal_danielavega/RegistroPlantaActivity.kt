package com.example.proyectofinal_danielavega

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.models.Planta
import com.example.proyectofinal_danielavega.models.enums.TipoPlanta
import java.util.Calendar

class RegistroPlantaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private var plantaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_planta)

        dbHelper = DbHelper(this)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloFormulario)
        val spTipoPlanta = findViewById<Spinner>(R.id.spTipoPlanta)
        val etNombrePlanta = findViewById<EditText>(R.id.etNombrePlanta)
        val etNombreCientifico = findViewById<EditText>(R.id.etNombreCientifico)
        val etFechaSiembra = findViewById<EditText>(R.id.etFechaSiembra)
        val etMetodoSiembra = findViewById<EditText>(R.id.etMetodoSiembra)
        val etObservaciones = findViewById<EditText>(R.id.etObservaciones)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPlanta)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // --- Spinner de TipoPlanta ---
        val tipos = TipoPlanta.values()
        val etiquetas = tipos.map { it.etiqueta }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoPlanta.adapter = spinnerAdapter

        // --- Selector de fecha ---
        etFechaSiembra.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anioActual = calendario.get(Calendar.YEAR)
            val mesActual = calendario.get(Calendar.MONTH)
            val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, anio, mes, dia ->
                val fechaFormateada = "%04d-%02d-%02d".format(anio, mes + 1, dia)
                etFechaSiembra.setText(fechaFormateada)
            }, anioActual, mesActual, diaActual).show()
        }

        // --- Detectar modo edición ---
        plantaId = intent.getIntExtra("plantaId", -1)

        if (plantaId != -1) {
            tvTitulo.text = "Editar Planta"

            val planta = dbHelper.obtenerPlantaPorId(plantaId)
            if (planta != null) {
                spTipoPlanta.setSelection(tipos.indexOf(planta.tipoPlanta))
                etNombrePlanta.setText(planta.nombrePlanta)
                etNombreCientifico.setText(planta.nombreCientifico)
                etFechaSiembra.setText(planta.fechaSiembra)
                etMetodoSiembra.setText(planta.metodoSiembra)
                etObservaciones.setText(planta.observaciones)
            }
        }

        // --- Guardar ---
        btnGuardar.setOnClickListener {
            val nombre = etNombrePlanta.text.toString().trim()
            val fechaSiembra = etFechaSiembra.text.toString().trim()

            if (nombre.isEmpty() || fechaSiembra.isEmpty()) {
                Toast.makeText(this, "Nombre y fecha de siembra son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipoElegido = tipos[spTipoPlanta.selectedItemPosition]

            val planta = Planta(
                plantaId = if (plantaId != -1) plantaId else 0,
                tipoPlanta = tipoElegido,
                nombrePlanta = nombre,
                nombreCientifico = etNombreCientifico.text.toString().trim().ifEmpty { null },
                observaciones = etObservaciones.text.toString().trim().ifEmpty { null },
                fechaSiembra = fechaSiembra,
                metodoSiembra = etMetodoSiembra.text.toString().trim().ifEmpty { null }
            )

            try {
                if (plantaId != -1) {
                    dbHelper.editarPlanta(planta)
                    Toast.makeText(this, "Planta actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertarPlanta(planta)
                    Toast.makeText(this, "Planta guardada", Toast.LENGTH_SHORT).show()
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar la planta", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}