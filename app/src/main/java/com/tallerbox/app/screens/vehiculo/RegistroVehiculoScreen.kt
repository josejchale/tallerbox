package com.tallerbox.app.screens.vehiculo

import com.tallerbox.app.model.vehiculo.Vehiculo
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RegistroVehiculoScreen(navController: NavHostController, clienteId: Int?) {
    var marca by remember { mutableStateOf(value = "") }
    var modelo by remember { mutableStateOf(value = "") }
    var ano by remember { mutableStateOf(value = "") }
    var color by remember { mutableStateOf(value = "") }
    var vin by remember { mutableStateOf(value = "") }
    var placa by remember { mutableStateOf(value = "") }

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
                val vehiculo = Vehiculo(
                    marca,
                    modelo,
                    ano,
                    color,
                    vin,
                    placa
                )
            }
        ) {
            Text(text = "Guardar Vehiculo")
        }
    }
}