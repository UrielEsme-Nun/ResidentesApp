package com.example.residenciaapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Resident(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val domicilio: String,
    val telefono: String,
    val password: String,
    val codigoQR: String // 👈 Este es el importante
)

@Serializable
data class Vehicle(
    val id: String,
    val marca: String,
    val modelo: String,
    val placas: String,
    val residenteId: String // clave foránea
)

@Serializable
data class Guest(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val tipoInvitacion: String,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val codigoQR: String,
    val residenteId: String  // <- este campo es importante
)