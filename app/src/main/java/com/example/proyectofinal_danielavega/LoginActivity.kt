package com.example.proyectofinal_danielavega

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.utils.Seguridad

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DbHelper(this)

        val etUsuario = findViewById<android.widget.EditText>(R.id.etUsuario)
        val etContrasena = findViewById<android.widget.EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<android.widget.Button>(R.id.btnIniciarSesion)
        val tvIrRegistro = findViewById<android.widget.TextView>(R.id.tvIrRegistro)

        btnIniciarSesion.setOnClickListener {
            val nombreUsuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val usuario = dbHelper.obtenerUsuarioPorNombre(nombreUsuario)
                val hashIngresado = Seguridad.hashearContrasena(contrasena)

                if (usuario != null && usuario.contrasenaHash == hashIngresado) {
                    val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                    prefs.edit().putString("rolActual", usuario.rolUsuario.name)
                        .putString("usuarioActual", usuario.nombreUsuario)
                        .apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))

        }
    }
}