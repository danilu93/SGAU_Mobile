package com.example.proyectofinal_danielavega.utils

import java.security.MessageDigest

// Objeto que se encarga de las funciones relacionadas con la seguridad
object Seguridad {

    // Convierte una contraseña en texto plano a un código seguro (hash)
    fun hashearContrasena(contrasena: String): String {
        // Se genera el hash usando el algoritmo SHA-256
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
        // Se convierte cada byte a formato hexadecimal para guardarlo como texto
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
