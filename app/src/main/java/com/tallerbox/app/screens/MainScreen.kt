package com.tallerbox.app.screens

import android.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tallerbox.app.components.MetricCard.MetricCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, top = 50.dp,),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Acciones Rápidas", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { navController.navigate("registro_cliente") }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Nuevo Cliente", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Cliente")
            }

            Button(onClick = { navController.navigate("registro_vehiculo") }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Nuevo Vehiculo", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Vehiculo")
            }

            Button(onClick = { navController.navigate("registro_orden") }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Nueva orden", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva Orden")
            }

            MetricCard( title = "Pendientes", value = "1", textColor = Red )
            MetricCard( title = "En proceso", value = "31",  textColor = Black )
            MetricCard( title = "Completados", value = "12", textColor = Green )
        }
    }
}
