package com.example.proyectofinal_danielavega

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.models.Planta
import com.example.proyectofinal_danielavega.models.enums.TipoPlanta
import com.example.proyectofinal_danielavega.utils.ToastPersonalizado
import java.util.Calendar

// Pantalla para registrar o editar una planta
class RegistroPlantaActivity : AppCompatActivity() {

    // Acceso a la base de datos
    private lateinit var dbHelper: DbHelper

    // Si plantaId es -1 es un registro nuevo, si no, es una edición
    private var plantaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_planta)

        // Conexión a la base de datos
        dbHelper = DbHelper(this)

        // Se obtienen los elementos de la pantalla
        val tvTitulo = findViewById<TextView>(R.id.tvTituloFormulario)
        val spTipoPlanta = findViewById<Spinner>(R.id.spTipoPlanta)
        val etNombrePlanta = findViewById<EditText>(R.id.etNombrePlanta)
        val etNombreCientifico = findViewById<EditText>(R.id.etNombreCientifico)
        val etFechaSiembra = findViewById<EditText>(R.id.etFechaSiembra)
        val etMetodoSiembra = findViewById<EditText>(R.id.etMetodoSiembra)
        val etObservaciones = findViewById<EditText>(R.id.etObservaciones)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPlanta)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // Casilla para mostrar u ocultar los campos opcionales
        val cbMostrarOpcionales = findViewById<android.widget.CheckBox>(R.id.cbMostrarOpcionales)
        val contenedorOpcionales = findViewById<android.widget.LinearLayout>(R.id.contenedorOpcionales)

        cbMostrarOpcionales.setOnCheckedChangeListener { _, marcado ->
            contenedorOpcionales.visibility = if (marcado) View.VISIBLE else View.GONE
        }

        // Spinner de TipoPlanta
        val tipos = TipoPlanta.values()
        val etiquetas = tipos.map { it.etiqueta }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoPlanta.adapter = spinnerAdapter

        // Selector de fecha
        etFechaSiembra.setOnClickListener {
            // Se usa la fecha actual como fecha inicial del calendario
            val calendario = Calendar.getInstance()
            val anioActual = calendario.get(Calendar.YEAR)
            val mesActual = calendario.get(Calendar.MONTH)
            val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

            // Al elegir un día se escribe la fecha en el campo
            DatePickerDialog(this, { _, anio, mes, dia ->
                val fechaFormateada = "%04d-%02d-%02d".format(anio, mes + 1, dia)
                etFechaSiembra.setText(fechaFormateada)
            }, anioActual, mesActual, diaActual).show()
        }

        // Detectar modo edición
        // Se revisa si la pantalla recibe un id de planta
        plantaId = intent.getIntExtra("plantaId", -1)

        if (plantaId != -1) {
            // Cambia el título y se llenan los campos con los datos de la planta
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

        // Guardar
        btnGuardar.setOnClickListener {
            val nombre = etNombrePlanta.text.toString().trim()
            val fechaSiembra = etFechaSiembra.text.toString().trim()

            // Nombre y fecha de siembra son obligatorios
            if (nombre.isEmpty() || fechaSiembra.isEmpty()) {
                ToastPersonalizado.mostrarToast(this, "Nombre y fecha de siembra son obligatorios")
                return@setOnClickListener
            }

            val tipoElegido = tipos[spTipoPlanta.selectedItemPosition]

            // Se arma el objeto Planta con los datos del formulario
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
                // Si hay id, se actualiza la planta, si no, se inserta una nueva
                if (plantaId != -1) {
                    dbHelper.editarPlanta(planta)
                    ToastPersonalizado.mostrarToast(this, "Planta actualizada")
                } else {
                    dbHelper.insertarPlanta(planta)
                    ToastPersonalizado.mostrarToast(this, "Planta guardada")
                }
                finish()
            } catch (e: Exception) {
                ToastPersonalizado.mostrarToast(this, "Error al guardar la planta")
                e.printStackTrace()
            }
        }

        // Botón para salir sin guardar
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
