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
import com.example.proyectofinal_danielavega.utils.ToastPersonalizado

// Pantalla de registro de un nuevo usuario
class RegistroUsuarioActivity : AppCompatActivity() {

    // Acceso a la base de datos
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        // Conexión a la base de datos
        dbHelper = DbHelper(this)

        // Se obtienen los elementos de la pantalla
        val etNombreUsuario = findViewById<android.widget.EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<android.widget.EditText>(R.id.etCorreo)
        val etContrasena = findViewById<android.widget.EditText>(R.id.etContrasena)
        val etConfirmarContrasena = findViewById<android.widget.EditText>(R.id.etConfirmarContrasena)
        val rgRol = findViewById<android.widget.RadioGroup>(R.id.rgRol)
        val btnRegistrarse = findViewById<android.widget.Button>(R.id.btnRegistrarse)
        val tvIrLogin = findViewById<android.widget.TextView>(R.id.tvIrLogin)

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

        // Casilla para mostrar u ocultar la confirmación de la contraseña
        val cbMostrarConfirmacion = findViewById<android.widget.CheckBox>(R.id.cbMostrarConfirmacion)
        cbMostrarConfirmacion.setOnCheckedChangeListener { _, marcado ->
            etConfirmarContrasena.transformationMethod = if (marcado) {
                android.text.method.HideReturnsTransformationMethod.getInstance()
            } else {
                android.text.method.PasswordTransformationMethod.getInstance()
            }
            etConfirmarContrasena.setSelection(etConfirmarContrasena.text.length)
        }

        // Al presionar el botón se valida y guarda el nuevo usuario
        btnRegistrarse.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()
            val confirmarContrasena = etConfirmarContrasena.text.toString()

            // Se verifica que los campos obligatorios no estén vacíos
            if (nombreUsuario.isEmpty() || contrasena.isEmpty()){
                etNombreUsuario.error = "Por favor, complete todos los campos"
                etNombreUsuario.requestFocus()
                return@setOnClickListener
            }

            // Se verifica que el correo tenga un formato válido
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Ingrese un correo electrónico válido"
                etCorreo.requestFocus()
                return@setOnClickListener
            }

            // La contraseña debe tener mínimo 6 caracteres, con letra y número
            val contrasenaRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
            if (!contrasenaRegex.matches(contrasena)) {
                etContrasena.error = "La contraseña debe tener al menos 6 caracteres, incluyendo una letra y un número"
                etContrasena.requestFocus()
                return@setOnClickListener
            }

            // Se verifica que las contraseñas coincidan
            if (contrasena != confirmarContrasena) {
                etConfirmarContrasena.error = "Las contraseñas no coinciden"
                etConfirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            // Se obtiene el rol seleccionado en los botones de opción
            val rolSeleccionado = when (rgRol.checkedRadioButtonId) {
                R.id.rbAdministrador -> RolUsuario.ADMINISTRADOR
                R.id.rbGestor -> RolUsuario.GESTORAGRICOLA
                else -> RolUsuario.CONSULTA
            }

            try {
                // Se verifica que el correo no esté ya registrado
                val correoExistente = dbHelper.obtenerUsuarioPorCorreo(correo)
                if (correoExistente != null) {
                    etCorreo.error = "El correo electrónico ya está en uso"
                    etCorreo.requestFocus()
                    return@setOnClickListener
                }

                // Se verifica que el nombre de usuario no esté ya registrado
                val usuarioExistente = dbHelper.obtenerUsuarioPorNombre(nombreUsuario)
                if (usuarioExistente != null) {
                    etNombreUsuario.error = "El nombre de usuario ya está en uso"
                    etNombreUsuario.requestFocus()
                    return@setOnClickListener
                }

                // Se crea el usuario con la contraseña encriptada
               val nuevoUsuario = Usuario(
                   nombreUsuario = nombreUsuario,
                   contrasenaHash = Seguridad.hashearContrasena(contrasena),
                   correoElectronico = correo,
                   rolUsuario = rolSeleccionado,
                   activo = true
               )

                // Se guarda el usuario en la base de datos
                dbHelper.insertarUsuario(nuevoUsuario)

                ToastPersonalizado.mostrarToast(this, "Registro exitoso, inicie sesión")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                ToastPersonalizado.mostrarToast(this, "Error al registrar usuario: ${e.message}")
                e.printStackTrace()
            }
        }

        // Texto que lleva a la pantalla de inicio de sesión
        tvIrLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
