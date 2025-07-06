package com.example.residenciaapp.network

import com.example.residenciaapp.Resident
import com.example.residenciaapp.Vehicle
import com.example.residenciaapp.Guest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiService {
    private const val BASE_URL = "https://TU_BACKEND_URL/api" // <- Aquí pondrás tu dirección más adelante

    // 🔐 Login de residente
    fun loginResidente(id: String, password: String): Resident? {
        val url = URL("$BASE_URL/residentes/login?id=$id&password=$password")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            if (responseCode == 200) {
                val json = inputStream.bufferedReader().readText()
                return Json.decodeFromString<Resident>(json)
            }
        }
        return null
    }

    // 🚗 Obtener vehículos por ID residente
    fun obtenerVehiculos(residenteId: String): List<Vehicle> {
        val url = URL("$BASE_URL/vehiculos/$residenteId")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            if (responseCode == 200) {
                val json = inputStream.bufferedReader().readText()
                return Json.decodeFromString(json)
            }
        }
        return emptyList()
    }

    // 🚗 Agregar vehículo
    fun agregarVehiculo(vehicle: Vehicle): Boolean {
        val url = URL("$BASE_URL/vehiculos")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            val jsonBody = Json.encodeToString(vehicle)
            OutputStreamWriter(outputStream).use { it.write(jsonBody) }

            return responseCode == 200 || responseCode == 201
        }
    }

    // 👥 Obtener invitados
    fun obtenerInvitados(residenteId: String): List<Guest> {
        val url = URL("$BASE_URL/invitados/$residenteId")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            if (responseCode == 200) {
                val json = inputStream.bufferedReader().readText()
                return Json.decodeFromString(json)
            }
        }
        return emptyList()
    }

    // 👥 Agregar invitado
    fun agregarInvitado(guest: Guest): Boolean {
        val url = URL("$BASE_URL/invitados")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            val jsonBody = Json.encodeToString(guest)
            OutputStreamWriter(outputStream).use { it.write(jsonBody) }

            return responseCode == 200 || responseCode == 201
        }
    }
}

