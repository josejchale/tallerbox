package com.tallerbox.app.screens.orden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.OrdenRepository
import com.tallerbox.app.viewmodel.orden.OrdenViewModel
import com.tallerbox.app.viewmodel.orden.OrdenViewModelFactory

@Composable
fun ListaOrdenesScreen(
    navController: NavHostController,
    vehiculoId: Int? = null,
    clienteId: Int? = null
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = OrdenRepository(db.ordenServicioDao())
    val vm: OrdenViewModel = viewModel(factory = OrdenViewModelFactory(repo))

    // 🔹 Determinar qué flujo usar según parámetros
    val ordenesFlow = when {
        vehiculoId != null -> vm.obtenerPorVehiculo(vehiculoId)
        clienteId != null -> vm.obtenerPorCliente(clienteId)
        else -> vm.ordenes
    }

    val ordenes by ordenesFlow.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Órdenes", style = MaterialTheme.typography.headlineSmall)

        if (ordenes.isEmpty()) {
            Text(
                "No hay órdenes disponibles",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // 🔹 Agrupar las órdenes por estado
            val grouped = ordenes.groupBy { it.estadoOrden?.lowercase() ?: "desconocido" }

            // 🔹 Colores por estado
            val estados = listOf(
                "pendiente" to Color(0xFFFCC6BB),
                "en proceso" to Color(0xFFFFEB69),
                "completada" to Color(0xFFA0FF69),
                "entregada" to Color(0xFFDBDBDB)
            )

            estados.forEach { (estado, color) ->
                var expanded by remember {
                    mutableStateOf(estado !in listOf("completada", "entregada"))
                }

                val ordenesEstado = grouped[estado.lowercase()] ?: emptyList()

                if (ordenesEstado.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column {
                            // 🔹 Encabezado de cada grupo (estado)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    estado.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Icon(
                                    imageVector = if (expanded)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }

                            if (expanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    ordenesEstado.forEach { orden ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    navController.navigate("detalle_orden/${orden.id}")
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = color
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = orden.numeroOrden,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Fecha: ${orden.fechaIngreso}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = orden.descripcionFalla ?: "",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Volver")
            }
            Button(
                onClick = {
                    navController.navigate("registro_orden/$clienteId/${vehiculoId}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Nueva orden")
            }
        }
    }
}
