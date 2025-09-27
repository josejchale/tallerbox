package com.tallerbox.app.screens.vehiculo

import android.widget.Toast
import com.tallerbox.app.model.vehiculo.Vehiculo
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.vehiculo.VehiculoDao
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegistroVehiculoScreen(navController: NavHostController, clienteId: Int?) {

    if (clienteId == null) {
        Text("Error: cliente no especificado")
        return
    }

    var marca by remember { mutableStateOf(value = "") }
    var modelo by remember { mutableStateOf(value = "") }
    var ano by remember { mutableStateOf(value = "") }
    var color by remember { mutableStateOf(value = "") }
    var vin by remember { mutableStateOf(value = "") }
    var placa by remember { mutableStateOf(value = "") }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val vehiculoDao = db.vehiculoDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Registro de Motos", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it},
            label = { Text( text= "Marca de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it},
            label = { Text( text= "Modelo de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ano,
            onValueChange = { ano = it},
            label = { Text( text= "Año de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = color,
            onValueChange = { color = it},
            label = { Text( text= "Color de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vin,
            onValueChange = { vin = it},
            label = { Text( text= "VIN") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it},
            label = { Text( text= "Placa de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val vehiculo = VehiculoEntity(
                    clienteId = clienteId,
                    marca = marca,
                    modelo= modelo,
                    ano=ano,
                    color=color,
                    vin=vin,
                    placa=placa
                )
                CoroutineScope(Dispatchers.IO).launch {
                    vehiculoDao.insertar(vehiculo)

                    //Muestra un toast y navega al registro de vehiculos
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Vehiculo registrado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("main")
                    }
                }
            }
        ) {
            Text(text = "Guardar Vehiculo")
        }
    }
}