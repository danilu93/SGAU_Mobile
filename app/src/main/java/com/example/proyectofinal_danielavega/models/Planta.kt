package com.example.proyectofinal_danielavega.models

import com.example.proyectofinal_danielavega.models.enums.TipoPlanta

// Clase que representa una planta en la aplicación
data class Planta (
    var plantaId: Int = 0,
    var tipoPlanta: TipoPlanta,
    var nombrePlanta: String,
    var nombreCientifico: String?=null,
    var observaciones: String?=null,
    var fechaRegistro: String?=null,
    var fechaSiembra: String,
    var metodoSiembra: String?=null

)