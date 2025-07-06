package com.example.residenciaapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.residenciaapp.ui.theme.ResidenciaAppTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import androidx.core.content.FileProvider
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

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
fun QRCode(data: String, modifier: Modifier = Modifier) {
    val size = 512
    val bitmap = remember(data) {
        val bits = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
        Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(x, y, if (bits[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
                }
            }
        }
    }

    AndroidView(
        factory = { ImageView(it).apply { setImageBitmap(bitmap) } },
        modifier = modifier.size(200.dp)
    )
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
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.Black) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
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
    val user = when (userId) {
        "123" -> Resident("123", "Juan", "Pérez", "Calle Falsa 123", "555-1234")
        else -> Resident("000", "Desconocido", "Desconocido", "Sin domicilio", "000-0000")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ID: ${user.id}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = "Nombre: ${user.nombre}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = "Apellidos: ${user.apellidos}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = "Domicilio: ${user.domicilio}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = "Teléfono: ${user.telefono}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Código QR del residente:",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        QRCode(data = user.id)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("vehicles/${user.id}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Vehículos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("guests/${user.id}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Invitados")
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(userId, navController)
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

data class Resident(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val domicilio: String,
    val telefono: String
)

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
            .verticalScroll(rememberScrollState())  // <-- Agregado para scroll
    ) {
        Text(
            "Vehículos del usuario $userId",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (vehicleList.isEmpty()) {
            Text("No hay vehículos registrados.", color = Color.Black)
        } else {
            vehicleList.forEach { vehicle ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${vehicle.id}", color = Color.Black)
                        Text("Marca: ${vehicle.marca}", color = Color.Black)
                        Text("Modelo: ${vehicle.modelo}", color = Color.Black)
                        Text("Placas: ${vehicle.placas}", color = Color.Black)
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

        OutlinedButton(
            onClick = { navController.popBackStack() },
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
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var placas by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Agregar vehículo para $userId", style = MaterialTheme.typography.titleLarge, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = placas,
            onValueChange = { placas = it },
            label = { Text("Placas", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val generatedId = "VEH_${System.currentTimeMillis()}"
                println("Nuevo vehículo → ID: $generatedId, Marca: $marca, Modelo: $modelo, Placas: $placas")
                navController.popBackStack()
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
                Guest("G002", "Ana", "López", "Temporal", "2025-07-01", "2025-07-10"), // Puedes cambiar fechas para probar
                Guest("G003", "Luis", "Martínez", "Temporal", "2025-06-01", "2025-07-01")
            )
            else -> emptyList()
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Invitados de $userId", style = MaterialTheme.typography.titleLarge, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        if (guestList.isEmpty()) {
            Text("No hay invitados registrados.", color = Color.Black)
        } else {
            guestList.forEach { guest ->

                val backgroundColor = when (guest.tipoInvitacion) {
                    "Permanente" -> Color(0xFF2196F3) // Azul
                    "Temporal" -> {
                        val today = LocalDate.now()
                        val fin = guest.fechaFin?.let { LocalDate.parse(it) }

                        if (fin != null && today.isAfter(fin)) {
                            Color(0xFFF44336) // Rojo (expirado)
                        } else {
                            Color(0xFF4CAF50) // Verde (vigente)
                        }
                    }
                    else -> Color.LightGray
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${guest.id}", color = Color.White)
                        Text("Nombre: ${guest.nombre} ${guest.apellidos}", color = Color.White)
                        Text("Tipo: ${guest.tipoInvitacion}", color = Color.White)
                        guest.fechaInicio?.let { Text("Desde: $it", color = Color.White) }
                        guest.fechaFin?.let { Text("Hasta: $it", color = Color.White) }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Código QR del invitado:", color = Color.White)
                        QRCode(data = "INV-${guest.id}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // 1. Generar el bitmap QR
                                val qrBitmap = QRCodeWriter().encode("INV-${guest.id}", BarcodeFormat.QR_CODE, 512, 512)
                                val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565).apply {
                                    for (x in 0 until 512) {
                                        for (y in 0 until 512) {
                                            setPixel(x, y, if (qrBitmap[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
                                        }
                                    }
                                }

                                // 2. Guardar el bitmap en cache y obtener Uri
                                val file = File(context.cacheDir, "${guest.id}_qr.png")
                                FileOutputStream(file).use {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                }
                                file.setReadable(true, false)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                                // 3. Crear el mensaje
                                val message = """
        Hola ${guest.nombre},
        Has sido registrado como invitado.
        ID: ${guest.id}
        Tipo: ${guest.tipoInvitacion}
        ${guest.fechaInicio?.let { "Desde: $it\n" } ?: ""}
        ${guest.fechaFin?.let { "Hasta: $it\n" } ?: ""}
    """.trimIndent()

                                // 4. Intent para WhatsApp normal
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    type = "image/png"
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    setPackage("com.whatsapp")
                                }

                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Intentar con WhatsApp Business
                                    intent.setPackage("com.whatsapp.w4b")
                                    try {
                                        context.startActivity(intent)
                                    } catch (ex: Exception) {
                                        Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enviar QR por WhatsApp")
                        }
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

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
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
    val generatedId = remember { "GUEST_${System.currentTimeMillis()}" }
    val id = generatedId
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
        Text(
            text = "Agregar invitado para $userId",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tipo de invitación: ", color = Color.Black)

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
                label = { Text("Fecha de inicio (YYYY-MM-DD)", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fechaFin,
                onValueChange = { fechaFin = it },
                label = { Text("Fecha de fin (YYYY-MM-DD)", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                println("Nuevo invitado → ID: $id, Nombre: $nombre $apellidos, Tipo: $tipoInvitacion, Inicio: $fechaInicio, Fin: $fechaFin")
                navController.popBackStack()
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

