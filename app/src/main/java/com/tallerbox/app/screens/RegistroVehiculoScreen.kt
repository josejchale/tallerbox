package com.tallerbox.app.screens

import android.R
import com.tallerbox.app.model.Vehiculo
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistroVehiculoScreen(){
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
    }
}