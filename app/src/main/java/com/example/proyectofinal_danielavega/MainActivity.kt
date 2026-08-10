package com.example.proyectofinal_danielavega

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_danielavega.adapters.PlantaAdapter
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.models.enums.TipoPlanta
import com.example.proyectofinal_danielavega.utils.ToastPersonalizado
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Pantalla principal donde se muestra la lista de plantas
class MainActivity : AppCompatActivity() {

    // Acceso a la base de datos y adaptador de la lista
    private lateinit var dbHelper: DbHelper
    private lateinit var plantaAdapter: PlantaAdapter

    // Rol del usuario que inició sesión
    private lateinit var rolActual: String

    // Tipo de planta elegido en el filtro
    private var tipoSeleccionado: TipoPlanta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Conexión a la base de datos
        dbHelper = DbHelper(this)

        // Se recupera el rol y el nombre del usuario de la sesión guardada
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        rolActual = prefs.getString("rolActual", "") ?: ""
        val esConsulta = rolActual == "CONSULTA"

        val nombreUsuarioActual = prefs.getString("usuarioActual", "") ?: ""
        val tvUsuarioActual = findViewById<android.widget.TextView>(R.id.tvUsuarioActual)
        tvUsuarioActual.text = nombreUsuarioActual

        // Se obtienen los elementos de la pantalla
        val etBuscarNombre = findViewById<EditText>(R.id.etBuscarNombre)
        val spTipoPlanta = findViewById<Spinner>(R.id.spTipoPlanta)
        val rvPlantas = findViewById<RecyclerView>(R.id.rvPlantas)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarPlanta)
        val rgOrden = findViewById<android.widget.RadioGroup>(R.id.rgOrden)

        // Cuando cambia el orden, se vuelve a cargar la lista
        rgOrden.setOnCheckedChangeListener { _, _ ->
            cargarPlantas(etBuscarNombre.text.toString())
        }

        // Configurar el RecyclerView (lista de plantas)
        plantaAdapter = PlantaAdapter(
            plantas = emptyList(),
            esConsulta = esConsulta,
            mostrarFecha = false,
            // Al tocar una planta se abre su detalle
            onVerDetalleClick = { planta ->
                val intent = Intent(this, DetallePlantaActivity::class.java)
                intent.putExtra("plantaId", planta.plantaId)
                startActivity(intent)
            },
            // Al tocar editar se abre el formulario con los datos de la planta
            onEditarClick = { planta ->
                val intent = Intent(this, RegistroPlantaActivity::class.java)
                intent.putExtra("plantaId", planta.plantaId)
                startActivity(intent)
            },
            // Al tocar eliminar se pide confirmación antes de borrar
            onEliminarClick = { planta ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Planta")
                    .setMessage("¿Estás seguro de que deseas eliminar esta planta?")
                    .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarPlanta(planta.plantaId)
                        ToastPersonalizado.mostrarToast(this, "Planta eliminada")
                cargarPlantas(etBuscarNombre.text.toString())
            }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvPlantas.layoutManager = LinearLayoutManager(this)
        rvPlantas.adapter = plantaAdapter

        // Configurar el Spinner de TipoPlanta (filtro)
        val opcionesSpinner = mutableListOf("Todos")
        opcionesSpinner.addAll(TipoPlanta.values().map { it.etiqueta })

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoPlanta.adapter = spinnerAdapter

        // Al elegir un tipo en el spinner se filtra la lista
        spTipoPlanta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tipoSeleccionado = if (position == 0) null else TipoPlanta.values()[position - 1]
                cargarPlantas(etBuscarNombre.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Búsqueda por nombre mientras se escribe
        etBuscarNombre.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cargarPlantas(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Botón agregar planta (oculto si es Consulta)
        if (esConsulta) {
            fabAgregar.visibility = View.GONE
        } else {
            fabAgregar.setOnClickListener {
                val intent = Intent(this, RegistroPlantaActivity::class.java)
                startActivity(intent)
            }
        }

        // Se carga la lista de plantas al abrir la pantalla
        cargarPlantas("")

        val btnCerrarSesion = findViewById<android.widget.TextView>(R.id.btnCerrarSesion)

        // Al cerrar sesión se borra la sesión guardada y se vuelve al login
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

    // Carga las plantas desde la base de datos aplicando filtros y orden
    private fun cargarPlantas(nombre: String) {
        try {
            // Se buscan las plantas según el nombre y el tipo elegido
            var lista = dbHelper.buscarPlantasPorFiltro(nombre, tipoSeleccionado)

            val rgOrden = findViewById<android.widget.RadioGroup>(R.id.rgOrden)
            val ordenPorFecha = rgOrden.checkedRadioButtonId == R.id.rbOrdenFecha

            // Se ordena la lista según la opción elegida
            lista = when (rgOrden.checkedRadioButtonId) {
                R.id.rbOrdenTipo -> lista.sortedBy { it.tipoPlanta.etiqueta }
                R.id.rbOrdenFecha -> lista.sortedBy { it.fechaSiembra }
                else -> lista.sortedBy { it.nombrePlanta }
            }

            // Se actualiza la lista en la pantalla
            plantaAdapter.actualizarLista(lista, ordenPorFecha)
        } catch (e: Exception) {
            ToastPersonalizado.mostrarToast(this, "Error al cargar las plantas")
            e.printStackTrace()
        }
    }

    // Se recarga la lista cada vez que la pantalla vuelve a estar visible
    override fun onResume() {
        super.onResume()
        cargarPlantas(findViewById<EditText>(R.id.etBuscarNombre).text.toString())
    }
}
