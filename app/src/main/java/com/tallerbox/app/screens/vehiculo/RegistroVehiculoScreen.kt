package com.tallerbox.app.screens.vehiculo

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    var errorMarca by remember {mutableStateOf(false)}
    var errorModelo by remember {mutableStateOf(false)}
    var errorAno by remember {mutableStateOf(false)}
    var errorVin by remember {mutableStateOf(false)}
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val vehiculoDao = db.vehiculoDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top=60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Registro de Motos", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = marca,
            onValueChange = {
                marca = it
                errorMarca = it.isBlank()
            },
            label = { Text( text= "Marca de la moto") },
            isError = errorMarca,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMarca) {
        Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = modelo,
            onValueChange = {
                modelo = it
                errorModelo = it.isBlank()
            },
            label = { Text( text= "Modelo de la moto") },
            isError=errorModelo,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorModelo) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = ano,
            onValueChange = {
                ano = it
                errorAno = it.isBlank()
            },
            label = { Text( text= "Año de la moto") },
            isError = errorAno,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorAno) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = color,
            onValueChange = { color = it},
            label = { Text( text= "Color de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vin,
            onValueChange = {
                vin = it
                errorVin = it.isBlank()
            },
            label = { Text( text= "VIN") },
            isError = errorVin,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorVin) {
            Text("Este campo no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it},
            label = { Text( text= "Placa de la moto") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick ={ mostrarDialogoCancelar = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
                Button(
                    onClick = {
                        errorMarca = marca.isBlank()
                        errorModelo = modelo.isBlank()
                        errorAno = ano.isBlank()
                        errorVin = vin.isBlank()

                        val camposValidos = listOf(
                            !errorMarca,
                            !errorModelo,
                            !errorAno,
                            !errorVin
                        ).all {it}
                        if (camposValidos){

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
                                    navController.navigate("registro_orden/{clienteId}/{vehiculoId}")
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)

                ) {  Text("Guardar Vehiculo")}

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


