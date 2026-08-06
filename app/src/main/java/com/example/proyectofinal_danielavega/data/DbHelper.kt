package com.example.proyectofinal_danielavega.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectofinal_danielavega.models.Planta
import com.example.proyectofinal_danielavega.models.Usuario
import com.example.proyectofinal_danielavega.models.enums.RolUsuario
import com.example.proyectofinal_danielavega.models.enums.TipoPlanta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Clase DbHelper para manejar la base de datos SQLite
class DbHelper (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "sgau_mobile.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS = "usuarios"
        const val TABLE_PLANTAS = "plantas"
    }

    // Método onCreate para crear las tablas de la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        val createUsuarios: String = """
            CREATE TABLE $TABLE_USUARIOS (
                usuarioId INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreUsuario TEXT NOT NULL,
                correoElectronico TEXT NOT NULL UNIQUE,
                contrasenaHash TEXT NOT NULL,
                rolUsuario TEXT NOT NULL,
                activo INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createPlantas: String = """
            CREATE TABLE $TABLE_PLANTAS (
                plantaId INTEGER PRIMARY KEY AUTOINCREMENT,
                tipoPlanta TEXT NOT NULL,
                nombrePlanta TEXT NOT NULL,
                nombreCientifico TEXT,
                observaciones TEXT,
                fechaRegistro TEXT,
                fechaSiembra TEXT NOT NULL,
                metodoSiembra TEXT
            )
        """.trimIndent()
        // Ejecutar las sentencias SQL para crear las tablas
        db.execSQL(createUsuarios)
        db.execSQL(createPlantas)
    }

    // Método onUpgrade para manejar la actualización de la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLANTAS")
        onCreate(db)
    }

    // Método para insertar un nuevo usuario en la tabla de usuarios
    fun insertarUsuario(usuario: Usuario): Long {
        val db = this.writableDatabase

        // Crear un objeto ContentValues para almacenar los valores del usuario
        val valores = ContentValues().apply {
            put("nombreUsuario", usuario.nombreUsuario)
            put("correoElectronico", usuario.correoElectronico)
            put("contrasenaHash", usuario.contrasenaHash)
            put("rolUsuario", usuario.rolUsuario.toString())
            put("activo", if (usuario.activo) 1 else 0)
        }
        // Insertar el usuario en la base de datos y obtener el ID generado
        val id = db.insert(TABLE_USUARIOS, null, valores)
        db.close()
        return id
    }

    // Método para obtener un usuario por su nombre de usuario
    fun obtenerUsuarioPorNombre (nombreUsuario: String): Usuario? {
        val db = this.readableDatabase
        var usuario: Usuario? = null

        // Crear un cursor para consultar la tabla de usuarios por nombre de usuario
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "nombreUsuario = ?",
            arrayOf(nombreUsuario),
            null,
            null,
            null
        )

        // Si el cursor tiene resultados, crear un objeto Usuario con los datos obtenidos
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId")),
                nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario")),
                correoElectronico = cursor.getString(cursor.getColumnIndexOrThrow("correoElectronico")),
                contrasenaHash = cursor.getString(cursor.getColumnIndexOrThrow("contrasenaHash")),
                rolUsuario = RolUsuario.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("rolUsuario"))),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
            )
        }
        // Cerrar el cursor y la base de datos antes de retornar el usuario
        cursor.close()
        db.close()
        return usuario
    }

    fun obtenerUsuarioPorCorreo(correoElectronico: String): Usuario? {
        val db = this.readableDatabase
        var usuario: Usuario? = null

        // Crear un cursor para consultar la tabla de usuarios por correo electrónico
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "correoElectronico = ?",
            arrayOf(correoElectronico),
            null,
            null,
            null
        )

        // Si el cursor tiene resultados, crear un objeto Usuario con los datos obtenidos
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId")),
                nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario")),
                correoElectronico = cursor.getString(cursor.getColumnIndexOrThrow("correoElectronico")),
                contrasenaHash = cursor.getString(cursor.getColumnIndexOrThrow("contrasenaHash")),
                rolUsuario = RolUsuario.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("rolUsuario"))),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
            )
        }
        // Cerrar el cursor y la base de datos antes de retornar el usuario
        cursor.close()
        db.close()
        return usuario
    }

    // Método para insertar una nueva planta en la tabla de plantas
    fun insertarPlanta(planta: Planta): Long {
        val db = this.writableDatabase
        val formatoRegistro = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val fechaHoy = formatoRegistro.format(Date())

        // Crear un objeto ContentValues para almacenar los valores de la planta
        val valores = ContentValues().apply {
            put("tipoPlanta", planta.tipoPlanta.toString())
            put("nombrePlanta", planta.nombrePlanta)
            put("nombreCientifico", planta.nombreCientifico)
            put("observaciones", planta.observaciones)
            put("fechaRegistro", fechaHoy)
            put("fechaSiembra", planta.fechaSiembra)
            put("metodoSiembra", planta.metodoSiembra)
        }
        // Insertar la planta en la base de datos y obtener el ID generado
        val id = db.insert(TABLE_PLANTAS, null, valores)
        db.close()
        return id
    }

    // Método para obtener todas las plantas de la tabla de plantas
    fun obtenerPlantas(): List<Planta> {
        val listaPlantas = mutableListOf<Planta>()
        val db = this.readableDatabase

        // Crear un cursor para consultar todas las plantas ordenadas por nombre
        val cursor = db.query(
            TABLE_PLANTAS,
            null,
            null,
            null,
            null,
            null,
            "nombrePlanta ASC"
        )

        // Recorrer el cursor y crear objetos Planta con los datos obtenidos
        while (cursor.moveToNext()) {
            val planta = Planta(
                plantaId = cursor.getInt(cursor.getColumnIndexOrThrow("plantaId")),
                tipoPlanta = TipoPlanta.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipoPlanta"))),
                nombrePlanta = cursor.getString(cursor.getColumnIndexOrThrow("nombrePlanta")),
                nombreCientifico = cursor.getString(cursor.getColumnIndexOrThrow("nombreCientifico")),
                observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones")),
                fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                fechaSiembra = cursor.getString(cursor.getColumnIndexOrThrow("fechaSiembra")),
                metodoSiembra = cursor.getString(cursor.getColumnIndexOrThrow("metodoSiembra"))
            )
            listaPlantas.add(planta)
        }
        cursor.close()
        db.close()
        return listaPlantas
    }

    // Método para buscar plantas por nombre y tipo de planta
    fun buscarPlantasPorFiltro(nombre: String? = null, tipo: TipoPlanta? = null): List<Planta> {
        val listaPlantas = mutableListOf<Planta>()
        val db = this.readableDatabase

        // Construir la cláusula WHERE y los argumentos según los filtros proporcionados
        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()

        if (!nombre.isNullOrBlank()) {
            condiciones.add("nombrePlanta LIKE ?")
            argumentos.add("%$nombre%")
        }

        if (tipo != null) {
            condiciones.add("tipoPlanta = ?")
            argumentos.add(tipo.name)
        }

        // Combinar las condiciones en una cláusula WHERE
        val whereClause = if (condiciones.isNotEmpty()) condiciones.joinToString(" AND ") else null

        // Crear un cursor para consultar las plantas según los filtros aplicados
        val cursor = db.query(
            TABLE_PLANTAS,
            null,
            whereClause,
            if (argumentos.isNotEmpty()) argumentos.toTypedArray() else null,
            null,
            null,
            "nombrePlanta ASC"
        )


        // Recorrer el cursor y crear objetos Planta con los datos obtenidos
        while (cursor.moveToNext()) {
            listaPlantas.add(
                Planta(
                    plantaId = cursor.getInt(cursor.getColumnIndexOrThrow("plantaId")),
                    tipoPlanta = TipoPlanta.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipoPlanta"))),
                    nombrePlanta = cursor.getString(cursor.getColumnIndexOrThrow("nombrePlanta")),
                    nombreCientifico = cursor.getString(cursor.getColumnIndexOrThrow("nombreCientifico")),
                    observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones")),
                    fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                    fechaSiembra = cursor.getString(cursor.getColumnIndexOrThrow("fechaSiembra")),
                    metodoSiembra = cursor.getString(cursor.getColumnIndexOrThrow("metodoSiembra"))
                )
            )
        }
        cursor.close()
        db.close()
        return listaPlantas
    }

    // Método para obtener una planta por su ID
    fun obtenerPlantaPorId(plantaId: Int): Planta? {
        val db = this.readableDatabase
        var planta: Planta? = null

        val cursor = db.query(
            TABLE_PLANTAS,
            null,
            "plantaId = ?",
            arrayOf(plantaId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            planta = Planta(
                plantaId = cursor.getInt(cursor.getColumnIndexOrThrow("plantaId")),
                tipoPlanta = TipoPlanta.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipoPlanta"))),
                nombrePlanta = cursor.getString(cursor.getColumnIndexOrThrow("nombrePlanta")),
                nombreCientifico = cursor.getString(cursor.getColumnIndexOrThrow("nombreCientifico")),
                observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones")),
                fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                fechaSiembra = cursor.getString(cursor.getColumnIndexOrThrow("fechaSiembra")),
                metodoSiembra = cursor.getString(cursor.getColumnIndexOrThrow("metodoSiembra"))
            )
        }
        cursor.close()
        db.close()
        return planta
    }

    // Método para editar una planta existente en la tabla de plantas
    fun editarPlanta(planta: Planta): Int {
        val db = this.writableDatabase

        val valores = ContentValues().apply {
            put("tipoPlanta", planta.tipoPlanta.name)
            put("nombrePlanta", planta.nombrePlanta)
            put("nombreCientifico", planta.nombreCientifico)
            put("observaciones", planta.observaciones)
            put("fechaSiembra", planta.fechaSiembra)
            put("metodoSiembra", planta.metodoSiembra)
        }

        // Actualizar la planta en la base de datos y obtener el número de filas afectadas
        val filasEditadas = db.update(
            TABLE_PLANTAS,
            valores,
            "plantaId = ?",
            arrayOf(planta.plantaId.toString())
        )

        db.close()
        return filasEditadas
    }

    // Método para eliminar una planta de la tabla de plantas por su ID
    fun eliminarPlanta(plantaId: Int): Int {
        val db = this.writableDatabase

        val filasEliminadas = db.delete(
            TABLE_PLANTAS,
            "plantaId = ?",
            arrayOf(plantaId.toString())
        )

        db.close()
        return filasEliminadas
    }
}
