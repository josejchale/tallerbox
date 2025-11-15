package com.tallerbox.app.screens.vehiculo

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tallerbox.app.viewmodel.vehiculo.VehiculoViewModel
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import com.tallerbox.app.viewmodel.vehiculo.VehiculoViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarVehiculoScreen(
    navController: NavHostController,
    clienteId: Int? = null,
    vehiculoId: Int,
    vm: VehiculoViewModel = viewModel(factory = VehiculoViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var clienteSeleccionadoId by remember { mutableStateOf(clienteId) }

    // Cargar datos existentes
    LaunchedEffect(vehiculoId) {
        CoroutineScope(Dispatchers.IO).launch {
            val vehiculo = vm.obtenerVehiculoPorId(vehiculoId)
            vehiculo?.let {
                marca = it.marca
                modelo = it.modelo
                ano = it.ano
                color = it.color
                vin = it.vin
                placa = it.placa ?: ""
                clienteSeleccionadoId = it.clienteId
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Editar Vehículo", style = MaterialTheme.typography.headlineSmall)


        // Campos editables (sin validación obligatoria)
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ano, onValueChange = { ano = it }, label = { Text("Año") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = vin, onValueChange = { vin = it }, label = { Text("VIN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { mostrarDialogoCancelar = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    val vehiculoActualizado = VehiculoEntity(
                        id = vehiculoId,
                        clienteId = clienteSeleccionadoId ?: 0, // 👈 si es null, usa 0 o maneja según tu lógica
                        marca = marca,
                        modelo = modelo,
                        ano = ano,
                        color = color,
                        vin = vin,
                        placa = placa
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            vm.actualizarVehiculo(vehiculoActualizado)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Vehículo actualizado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        } catch (_: Exception) {
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Error al actualizar vehículo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar cambios")
            }
        }
    }

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("¿Salir sin guardar?") },
            text = { Text("¿Está seguro de cerrar esta ventana y descartar los datos del formulario?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoCancelar = false
                    navController.popBackStack()
                }) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("No")
                }
            }
        )
    }
}




