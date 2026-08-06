package com.example.proyectofinal_danielavega

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_danielavega.adapters.PlantaAdapter
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.models.enums.TipoPlanta
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var plantaAdapter: PlantaAdapter
    private lateinit var rolActual: String

    private var tipoSeleccionado: TipoPlanta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(this)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        rolActual = prefs.getString("rolActual", "") ?: ""
        val esConsulta = rolActual == "CONSULTA"

        val nombreUsuarioActual = prefs.getString("usuarioActual", "") ?: ""
        val tvUsuarioActual = findViewById<android.widget.TextView>(R.id.tvUsuarioActual)
        tvUsuarioActual.text = nombreUsuarioActual

        val etBuscarNombre = findViewById<EditText>(R.id.etBuscarNombre)
        val spTipoPlanta = findViewById<Spinner>(R.id.spTipoPlanta)
        val rvPlantas = findViewById<RecyclerView>(R.id.rvPlantas)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarPlanta)

        // --- Configurar el RecyclerView ---
        plantaAdapter = PlantaAdapter(
            plantas = emptyList(),
            esConsulta = esConsulta,
            onVerDetalleClick = { planta ->
                val intent = Intent(this, DetallePlantaActivity::class.java)
                intent.putExtra("plantaId", planta.plantaId)
                startActivity(intent)
            },
            onEditarClick = { planta ->
                val intent = Intent(this, RegistroPlantaActivity::class.java)
                intent.putExtra("plantaId", planta.plantaId)
                startActivity(intent)
            },
            onEliminarClick = { planta ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Planta")
                    .setMessage("¿Estás seguro de que deseas eliminar esta planta?")
                    .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarPlanta(planta.plantaId)
                Toast.makeText(this, "Planta eliminada", Toast.LENGTH_SHORT).show()
                cargarPlantas(etBuscarNombre.text.toString())
            }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvPlantas.layoutManager = LinearLayoutManager(this)
        rvPlantas.adapter = plantaAdapter

        // --- Configurar el Spinner de TipoPlanta ---
        val opcionesSpinner = mutableListOf("Todos")
        opcionesSpinner.addAll(TipoPlanta.values().map { it.etiqueta })

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoPlanta.adapter = spinnerAdapter

        spTipoPlanta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tipoSeleccionado = if (position == 0) null else TipoPlanta.values()[position - 1]
                cargarPlantas(etBuscarNombre.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- Búsqueda por nombre mientras se escribe ---
        etBuscarNombre.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cargarPlantas(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // --- Botón agregar planta (oculto si es Consulta) ---
        if (esConsulta) {
            fabAgregar.visibility = View.GONE
        } else {
            fabAgregar.setOnClickListener {
                val intent = Intent(this, RegistroPlantaActivity::class.java)
                startActivity(intent)
            }
        }

        cargarPlantas("")

        val btnCerrarSesion = findViewById<android.widget.TextView>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->

            val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun cargarPlantas(nombre: String) {
        try {
            val lista = dbHelper.buscarPlantasPorFiltro(nombre, tipoSeleccionado)
            plantaAdapter.actualizarLista(lista)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar las plantas", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }


    override fun onResume() {
        super.onResume()
        cargarPlantas(findViewById<EditText>(R.id.etBuscarNombre).text.toString())
    }
}