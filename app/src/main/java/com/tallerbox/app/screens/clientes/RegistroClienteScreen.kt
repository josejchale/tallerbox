package com.tallerbox.app.screens.clientes
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.viewmodel.cliente.ClienteViewModel
import com.tallerbox.app.viewmodel.cliente.ClienteViewModelFactory
import com.tallerbox.app.utils.estados
import com.tallerbox.app.utils.municipiosPorEstado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteScreen(navController: NavController, vm: ClienteViewModel = viewModel(factory = ClienteViewModelFactory(
    LocalContext.current
)
)) {    var nombreCompleto by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numeroCasa by remember { mutableStateOf("") }
    var calle1 by remember { mutableStateOf("") }
    var calle2 by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("") }
    var municipioSeleccionado by remember { mutableStateOf("") }
    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    val municipiosDisponibles = municipiosPorEstado[estadoSeleccionado] ?: emptyList()
    var telefono by remember { mutableStateOf("") }

    //Errores de campos vacios

    var errorTelefono by remember { mutableStateOf(false) }
    var errorNombre by remember { mutableStateOf(false) }
    var errorCalle by remember { mutableStateOf(false) }
    var errorEstado by remember { mutableStateOf(false) }
    var errorMunicipio by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val clienteDao = db.clienteDao()
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top=60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro de Cliente",
            style = MaterialTheme.typography.headlineSmall,

        )

        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = {
                nombreCompleto = it
                errorNombre = it.isBlank()
            },
            label = { Text("Nombre completo") },
            isError = errorNombre,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorNombre) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Dirección", style = MaterialTheme.typography.labelMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = calle,
                onValueChange = {
                    calle = it
                    errorCalle = it.isBlank()
                },
                label = { Text("Calle") },
                isError = errorCalle,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = numeroCasa,
                onValueChange = { numeroCasa = it },
                label = { Text("No.") },
                modifier = Modifier.weight(1f)
            )
        }
        if (errorCalle) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Cruzamientos", style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = calle1,
                onValueChange = { calle1 = it },
                label = { Text("Calle 1") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = calle2,
                onValueChange = { calle2 = it },
                label = { Text("Calle 2") },
                modifier = Modifier.weight(1f)
            )
        }


        ExposedDropdownMenuBox(
            expanded = expandedEstado,
            onExpandedChange = { expandedEstado = !expandedEstado }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                isError = errorEstado,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            municipioSeleccionado = "" // reset municipio
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        if (estadoSeleccionado.isNotBlank()) {
            ExposedDropdownMenuBox(
                expanded = expandedMunicipio,
                onExpandedChange = { expandedMunicipio = !expandedMunicipio }
            ) {
                OutlinedTextField(
                    value = municipioSeleccionado,
                    onValueChange = {
                        municipioSeleccionado = it
                        errorMunicipio = it.isBlank()
                    },
                    label = { Text("Municipio") },
                    isError = errorMunicipio,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMunicipio)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )


                ExposedDropdownMenu(
                    expanded = expandedMunicipio,
                    onDismissRequest = { expandedMunicipio = false }
                ) {
                    municipiosDisponibles.forEach { municipio ->
                        DropdownMenuItem(
                            text = { Text(municipio) },
                            onClick = {
                                municipioSeleccionado = municipio
                                expandedMunicipio = false
                            }
                        )
                    }
                }
            }
        }
        if (errorEstado || errorMunicipio) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Télefono", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errorTelefono = it.length != 10 || !it.all { c -> c.isDigit() }
            },
            label = { Text("Teléfono (10 dígitos)") },
            isError = errorTelefono,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (errorTelefono) {
            Text(
                text = "El teléfono debe tener 10 dígitos numéricos",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { mostrarDialogoCancelar = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                        errorNombre = nombreCompleto.isBlank()
                        errorCalle = calle.isBlank()
                        errorEstado = estadoSeleccionado.isBlank()
                        errorMunicipio = municipioSeleccionado.isBlank()
                        errorTelefono = telefono.length != 10 || !telefono.all { it.isDigit() }

                        val camposValidos = listOf(
                            !errorNombre,
                            !errorCalle,
                            !errorEstado,
                            !errorMunicipio,
                            !errorTelefono
                        ).all { it }

                        if (camposValidos) {
                            val nuevoCliente = ClienteEntity(
                                nombreCompleto = nombreCompleto,
                                calle = calle,
                                numeroCasa = numeroCasa.ifBlank { null },
                                calle1 = calle1,
                                calle2 = calle2,
                                estado = estadoSeleccionado,
                                municipio = municipioSeleccionado,
                                telefono = telefono
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val newIdLong = clienteDao.insertar(nuevoCliente) // devuelve Long
                                    val newId = newIdLong.toInt() // convierte a Int
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(context, "Cliente registrado", Toast.LENGTH_SHORT).show()
                                        // Navega a RegistroVehiculo con el id recién creado
                                        navController.navigate("registro_vehiculo/$newId")
                                    }
                                } catch (e: Exception) {
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(context, "Error al guardar cliente", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar Cliente")
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
}
