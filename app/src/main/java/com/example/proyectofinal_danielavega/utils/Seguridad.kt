package com.example.proyectofinal_danielavega.utils

import java.security.MessageDigest

object Seguridad {

    fun hashearContrasena(contrasena: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
