package com.example.proyectofinal_danielavega.models

import com.example.proyectofinal_danielavega.models.enums.RolUsuario

// Clase que representa un usuario en la aplicación
data class Usuario(
    var usuarioId: Int = 0,
    var nombreUsuario: String,
    var correoElectronico: String,
    var contrasenaHash: String,
    var rolUsuario: RolUsuario,
    var activo: Boolean = true
)
