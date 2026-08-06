package com.example.proyectofinal_danielavega

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal_danielavega.data.DbHelper
import com.example.proyectofinal_danielavega.models.Usuario
import com.example.proyectofinal_danielavega.models.enums.RolUsuario
import com.example.proyectofinal_danielavega.utils.Seguridad

class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        dbHelper = DbHelper(this)


        val etNombreUsuario = findViewById<android.widget.EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<android.widget.EditText>(R.id.etCorreo)
        val etContrasena = findViewById<android.widget.EditText>(R.id.etContrasena)
        val etConfirmarContrasena = findViewById<android.widget.EditText>(R.id.etConfirmarContrasena)
        val rgRol = findViewById<android.widget.RadioGroup>(R.id.rgRol)
        val btnRegistrarse = findViewById<android.widget.Button>(R.id.btnRegistrarse)
        val tvIrLogin = findViewById<android.widget.TextView>(R.id.tvIrLogin)

        btnRegistrarse.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()
            val confirmarContrasena = etConfirmarContrasena.text.toString()

            if (nombreUsuario.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this, "Por favor, complete todos los campos", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rolSeleccionado = when (rgRol.checkedRadioButtonId) {
                R.id.rbAdministrador -> RolUsuario.ADMINISTRADOR
                R.id.rbGestor -> RolUsuario.GESTORAGRICOLA
                else -> RolUsuario.CONSULTA
            }

            try {
                val correoExistente = dbHelper.obtenerUsuarioPorCorreo(correo)
                if (correoExistente != null) {
                    Toast.makeText(this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val usuarioExistente = dbHelper.obtenerUsuarioPorNombre(nombreUsuario)
                if (usuarioExistente != null) {
                    Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

               val nuevoUsuario = Usuario(
                   nombreUsuario = nombreUsuario,
                   contrasenaHash = Seguridad.hashearContrasena(contrasena),
                   correoElectronico = correo,
                   rolUsuario = rolSeleccionado,
                   activo = true
               )

                dbHelper.insertarUsuario(nuevoUsuario)

                Toast.makeText(this, "Registro exitoso, inicie sesión", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        tvIrLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}