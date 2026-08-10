package com.example.proyectofinal_danielavega

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.utils.Seguridad
import com.example.proyectofinal_danielavega.utils.ToastPersonalizado

// Pantalla de inicio de sesión
class LoginActivity : AppCompatActivity() {

    // Acceso a la base de datos
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Conexión a la base de datos
        dbHelper = DbHelper(this)

        // Se obtienen los elementos de la pantalla
        val etUsuario = findViewById<android.widget.EditText>(R.id.etUsuario)
        val etContrasena = findViewById<android.widget.EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<android.widget.Button>(R.id.btnIniciarSesion)
        val tvIrRegistro = findViewById<android.widget.TextView>(R.id.tvIrRegistro)

        // Casilla para mostrar u ocultar la contraseña
        val cbMostrarContrasena = findViewById<android.widget.CheckBox>(R.id.cbMostrarContrasena)
        cbMostrarContrasena.setOnCheckedChangeListener { _, marcado ->
            etContrasena.transformationMethod = if (marcado) {
                android.text.method.HideReturnsTransformationMethod.getInstance()
            } else {
                android.text.method.PasswordTransformationMethod.getInstance()
            }
            etContrasena.setSelection(etContrasena.text.length)
        }

        // Al presionar el botón se valida el usuario y la contraseña
        btnIniciarSesion.setOnClickListener {
            val nombreUsuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            // Se verifica que no haya campos vacíos
            if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
                ToastPersonalizado.mostrarToast(this, "Por favor, complete todos los campos")
                return@setOnClickListener
            }

            try {
                // Se busca el usuario y se compara la contraseña encriptada
                val usuario = dbHelper.obtenerUsuarioPorNombre(nombreUsuario)
                val hashIngresado = Seguridad.hashearContrasena(contrasena)

                // Si el usuario existe y la contraseña coincide, se inicia sesión
                if (usuario != null && usuario.contrasenaHash == hashIngresado) {
                    // Se guarda la sesión con el rol y el nombre del usuario
                    val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                    prefs.edit().putString("rolActual", usuario.rolUsuario.name)
                        .putString("usuarioActual", usuario.nombreUsuario)
                        .apply()

                    // Se pasa a la pantalla principal
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    ToastPersonalizado.mostrarToast(this, "Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                ToastPersonalizado.mostrarToast(this, "Error al iniciar sesión: ${e.message}")
                e.printStackTrace()
            }
        }

        // Texto que lleva a la pantalla de registro
        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))
        }
    }
}
