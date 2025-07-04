package com.example.residenciaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.residenciaapp.ui.theme.ResidenciaAppTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResidenciaAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("home/$id")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}

@Composable
fun HomeScreen(userId: String, navController: NavHostController) {
    // Datos simulados, luego los obtendrás desde base de datos
    val user = when (userId) {
        "123" -> Resident("123", "Juan", "Pérez", "Calle Falsa 123", "555-1234")
        else -> Resident("000", "Desconocido", "Desconocido", "Sin domicilio", "000-0000")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("ID: ${user.id}")
        Text("Nombre: ${user.nombre}")
        Text("Apellidos: ${user.apellidos}")
        Text("Domicilio: ${user.domicilio}")
        Text("Teléfono: ${user.telefono}")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("vehicles/${user.id}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Vehículos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("guests/${user.id}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Invitados")
        }
    }
}

data class Resident(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val domicilio: String,
    val telefono: String
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("home/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(userId,navController)
        }
        composable("vehicles/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            VehicleScreen(userId, navController)
        }
        composable("add_vehicle/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            AddVehicleScreen(userId, navController)
        }
        composable("guests/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            GuestScreen(userId, navController)
        }
        composable("add_guest/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            AddGuestScreen(userId, navController)
        }
    }
}

@Composable
fun VehicleScreen(userId: String, navController: NavHostController) {
    // Simulación de vehículos por usuario
    val vehicleList = remember {
        when (userId) {
            "123" -> listOf(
                Vehicle("V001", "Toyota", "Corolla", "ABC-123"),
                Vehicle("V002", "Honda", "Civic", "XYZ-789")
            )
            else -> emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Vehículos del usuario $userId", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (vehicleList.isEmpty()) {
            Text("No hay vehículos registrados.")
        } else {
            vehicleList.forEach { vehicle ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${vehicle.id}")
                        Text("Marca: ${vehicle.marca}")
                        Text("Modelo: ${vehicle.modelo}")
                        Text("Placas: ${vehicle.placas}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("add_vehicle/$userId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar vehículo")
        }

        Button(
            onClick = { /* navController.popBackStack() más adelante */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}

data class Vehicle(
    val id: String,
    val marca: String,
    val modelo: String,
    val placas: String
)

@Composable
fun AddVehicleScreen(userId: String, navController: NavHostController) {
    var id by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var placas by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Agregar vehículo para $userId", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID del vehículo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = placas,
            onValueChange = { placas = it },
            label = { Text("Placas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                println("Nuevo vehículo → ID: $id, Marca: $marca, Modelo: $modelo, Placas: $placas")
                navController.popBackStack() // Regresa a la pantalla anterior
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
fun GuestScreen(userId: String, navController: NavHostController) {
    val guestList = remember {
        when (userId) {
            "123" -> listOf(
                Guest("G001", "Carlos", "Ramírez", "Permanente", "2025-01-01", null),
                Guest("G002", "Ana", "López", "Temporal", "2025-07-01", "2025-07-05")
            )
            else -> emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Invitados de $userId", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (guestList.isEmpty()) {
            Text("No hay invitados registrados.")
        } else {
            guestList.forEach { guest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${guest.id}")
                        Text("Nombre: ${guest.nombre} ${guest.apellidos}")
                        Text("Tipo: ${guest.tipoInvitacion}")
                        guest.fechaInicio?.let { Text("Desde: $it") }
                        guest.fechaFin?.let { Text("Hasta: $it") }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("add_guest/$userId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar invitado")
        }
    }
}

data class Guest(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val tipoInvitacion: String,
    val fechaInicio: String? = null,
    val fechaFin: String? = null
)

@Composable
fun AddGuestScreen(userId: String, navController: NavHostController) {
    var id by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var tipoInvitacion by remember { mutableStateOf("Permanente") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }

    val mostrarFechas = tipoInvitacion == "Temporal"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Agregar invitado para $userId", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID del invitado") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selección de tipo de invitación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tipo de invitación: ")

            Spacer(modifier = Modifier.width(16.dp))

            DropdownMenuTipoInvitacion(
                tipoActual = tipoInvitacion,
                onTipoSelected = { tipoInvitacion = it }
            )
        }

        if (mostrarFechas) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = { fechaInicio = it },
                label = { Text("Fecha de inicio (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fechaFin,
                onValueChange = { fechaFin = it },
                label = { Text("Fecha de fin (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                println("Nuevo invitado → ID: $id, Nombre: $nombre $apellidos, Tipo: $tipoInvitacion, Inicio: $fechaInicio, Fin: $fechaFin")
                navController.popBackStack() // Regresar
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
fun DropdownMenuTipoInvitacion(
    tipoActual: String,
    onTipoSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val tipos = listOf("Permanente", "Temporal")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(tipoActual)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tipos.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo) },
                    onClick = {
                        onTipoSelected(tipo)
                        expanded = false
                    }
                )
            }
        }
    }
}

