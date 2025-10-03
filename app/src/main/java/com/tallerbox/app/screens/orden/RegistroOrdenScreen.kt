package com.tallerbox.app.screens.orden

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.orden.*
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import com.tallerbox.app.model.cliente.ClienteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroOrdenScreen(
    navController: NavHostController,
    clienteId: Int? = null,
    vehiculoId: Int? = null
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val clienteDao = db.clienteDao()
    val vehiculoDao = db.vehiculoDao()
    val ordenDao = db.ordenServicioDao()

    var numeroOrden by remember { mutableStateOf("ORD-${System.currentTimeMillis()}") }
    val dateFormatter = remember { SimpleDateFormat("EEE, dd MMM", Locale("es", "MX")) }
    var fechaIngreso by remember { mutableStateOf(Date()) }
    var fechaEntrega by remember { mutableStateOf<Date?>(null) }
    var showIngresoPicker by remember { mutableStateOf(false) }
    var showEntregaPicker by remember { mutableStateOf(false) }
    var descripcion by remember { mutableStateOf("") }
    var trabajoRealizado by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    // condiciones: usar enum EstadoCondicion (SI/NO/ROTO)
    var espejos by remember { mutableStateOf(EstadoCondicion.NO) }
    var asientos by remember { mutableStateOf(EstadoCondicion.NO) }
    var faroDelantero by remember { mutableStateOf(EstadoCondicion.NO) }
    var luzTrasera by remember { mutableStateOf(EstadoCondicion.NO) }
    var direccionales by remember { mutableStateOf(EstadoCondicion.NO) }
    var cubiertas by remember { mutableStateOf(EstadoCondicion.NO) }
    var taponGasolina by remember { mutableStateOf(EstadoCondicion.NO) }
    var pedales by remember { mutableStateOf(EstadoCondicion.NO) }
    var parabrisas by remember { mutableStateOf(EstadoCondicion.NO) }
    var claxon by remember { mutableStateOf(EstadoCondicion.NO) }
    var taponAceite by remember { mutableStateOf(EstadoCondicion.NO) }
    var taponRadiador by remember { mutableStateOf(EstadoCondicion.NO) }
    var filtroAire by remember { mutableStateOf(EstadoCondicion.NO) }
    var bateria by remember { mutableStateOf(EstadoCondicion.NO) }
    var llaves by remember { mutableStateOf(EstadoCondicion.NO) }
    var observacionesCond by remember { mutableStateOf("") }

    // costo
    var costo by remember { mutableStateOf("0.0") }


    var clientes by remember { mutableStateOf<List<ClienteEntity>>(emptyList()) }
    var vehiculos by remember { mutableStateOf<List<VehiculoEntity>>(emptyList()) }

    var selectedClienteId by remember { mutableStateOf(clienteId) }
    var selectedVehiculoId by remember { mutableStateOf(vehiculoId) }

    var aceptaPublicidad by remember { mutableStateOf(false) }
    var aceptaCedencia by remember { mutableStateOf(false) }

    fun formatDate(date: Date?): String {
        return date?.let { SimpleDateFormat("EEE, d MMM yyyy", Locale("es", "ES")).format(it) } ?: "--"
    }
    LaunchedEffect(Unit) {
        // cargar listas iniciales
        clienteDao.obtenerTodosFlow().collect { list ->
            clientes = list
            if (clienteId != null && selectedClienteId == null && list.isNotEmpty()) {
                selectedClienteId = clienteId
            }
        }
    }

    LaunchedEffect(selectedClienteId) {
        if (selectedClienteId != null) {
            vehiculoDao.obtenerPorClienteFlow(selectedClienteId!!).collect { list ->
                vehiculos = list
                if (selectedVehiculoId == null && list.isNotEmpty()) selectedVehiculoId = list.first().id
            }
        } else {
            vehiculos = emptyList()
            selectedVehiculoId = null
        }
    }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
            .padding(horizontal = 20.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )

    {
        Text("Registro de Orden de Servicio", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = numeroOrden,
            onValueChange = { numeroOrden = it },
            label = { Text("Número de orden") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Fecha de ingreso", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showIngresoPicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha ingreso")
            }
            Text(dateFormatter.format(fechaIngreso), style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { showIngresoPicker = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir calendario")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Fecha de entrega estimada", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showEntregaPicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha entrega")
            }
            Text(
                fechaEntrega?.let { dateFormatter.format(it) } ?: "Sin definir",
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { showEntregaPicker = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir calendario")
            }
        }


        if (clienteId == null) {
            Text("Seleccionar cliente", style = MaterialTheme.typography.labelMedium)
            var clienteExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = clienteExpanded,
                onExpandedChange = { clienteExpanded = it }
            ) {
                OutlinedTextField(
                    value = clientes.find { it.id == selectedClienteId }?.nombreCompleto ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cliente") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = clienteExpanded,
                    onDismissRequest = { clienteExpanded = false }
                ) {
                    clientes.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.nombreCompleto) },
                            onClick = {
                                selectedClienteId = c.id
                                clienteExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (vehiculoId == null) {
            Text("Seleccionar vehículo", style = MaterialTheme.typography.labelMedium)
            var vehiculoExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = vehiculoExpanded,
                onExpandedChange = { vehiculoExpanded = it }
            ) {
                OutlinedTextField(
                    value = vehiculos.find { it.id == selectedVehiculoId }?.let { "${it.marca} ${it.modelo}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehículo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehiculoExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = vehiculoExpanded,
                    onDismissRequest = { vehiculoExpanded = false }
                ) {
                    vehiculos.forEach { v ->
                        DropdownMenuItem(
                            text = { Text("${v.marca} ${v.modelo}") },
                            onClick = {
                                selectedVehiculoId = v.id
                                vehiculoExpanded = false
                            }
                        )
                    }
                }
            }
        }


        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción de falla") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = trabajoRealizado,
            onValueChange = { trabajoRealizado = it },
            label = { Text("Trabajo propuesto / realizado") },
            modifier = Modifier.fillMaxWidth()
        )

        // Condiciones
        Text("Condiciones del vehículo", style = MaterialTheme.typography.titleMedium)
        CondicionRow("Espejos", espejos) { espejos = it }
        CondicionRow("Asientos", asientos) { asientos = it }
        CondicionRow("Faro delantero", faroDelantero) { faroDelantero = it }
        CondicionRow("Luz trasera", luzTrasera) { luzTrasera = it }
        CondicionRow("Direccionales", direccionales) { direccionales = it }
        CondicionRow("Cubiertas", cubiertas) { cubiertas = it }
        CondicionRow("Tapon gasolina", taponGasolina) { taponGasolina = it }
        CondicionRow("Pedales", pedales) { pedales = it }
        CondicionRow("Parabrisas", parabrisas) { parabrisas = it }
        CondicionRow("Claxon", claxon) { claxon = it }
        CondicionRow("Tapon aceite", taponAceite) { taponAceite = it }
        CondicionRow("Tapon radiador", taponRadiador) { taponRadiador = it }
        CondicionRow("Filtro aire", filtroAire) { filtroAire = it }
        CondicionRow("Batería", bateria) { bateria = it }
        CondicionRow("Llaves", llaves) { llaves = it }

        OutlinedTextField(
            value = observacionesCond,
            onValueChange = { observacionesCond = it },
            label = { Text("Observaciones condiciones") },
            modifier = Modifier.fillMaxWidth()
        )

        // Costos
        Text("Costos", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptaPublicidad,
                onCheckedChange = { aceptaPublicidad = it }
            )
            Text("Acepto recibir publicidad")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptaCedencia,
                onCheckedChange = { aceptaCedencia = it }
            )
            Text("Acepta que el prestador del servicio pueda ceder o transmitir el vehículo,sus partes o piezas, a terceros (como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de costos y precios. Esto será permitido únicamente en caso de ser estrictamente necesario y siempre que el propietario sea previamente informado de estas acciones y haya dado su consentimiento para el traslado del vehículo o de sus componentes.  (obligatorio)")
        }


        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (!aceptaCedencia) {
                        Toast.makeText(context, "Debes aceptar la cesión del vehiculo", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedClienteId == null) {
                        Toast.makeText(context, "Selecciona un cliente", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // construir entidad
                    val orden = OrdenServicioEntity(
                        clienteId = selectedClienteId!!,
                        vehiculoId = selectedVehiculoId,
                        numeroOrden = numeroOrden.ifBlank { "ORD-${System.currentTimeMillis()}" },
                        fechaIngreso = Date(),
                        fechaEntregaEstimado = null,
                        fechaEntregaReal = null,
                        descripcionFalla = descripcion,
                        trabajoRealizado = trabajoRealizado,
                        notas = notas,
                        condiciones = CondicionVehiculo(
                            espejos = espejos,
                            asientos = asientos,
                            faroDelantero = faroDelantero,
                            luzTrasera = luzTrasera,
                            direccionales = direccionales,
                            cubiertas = cubiertas,
                            taponGasolina = taponGasolina,
                            pedales = pedales,
                            parabrisas = parabrisas,
                            claxon = claxon,
                            taponAceite = taponAceite,
                            taponRadiador = taponRadiador,
                            filtroAire = filtroAire,
                            bateria = bateria,
                            llaves = llaves,
                            observaciones = observacionesCond.ifBlank { null }
                        ),
                        costos = CostosOrden(
                            costo = costo.toDoubleOrNull() ?: 0.0
                        ),
                        firmaClienteBase64 = null,
                        aceptaEnvioPublicidad = aceptaPublicidad,
                        aceptaCedencia = aceptaCedencia,
                        estadoOrden = "PENDIENTE"
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val newId = ordenDao.insertar(orden)
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Orden guardada", Toast.LENGTH_SHORT).show()
                            navController.navigate("detalle_orden/$newId")
                        }
                    }
                },
                enabled = aceptaCedencia,
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar Orden")
            }

        }
    }
    if (showIngresoPicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                fechaIngreso = cal.time
                showIngresoPicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showEntregaPicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                fechaEntrega = cal.time
                showEntregaPicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

}

@Composable
private fun CondicionRow(label: String, value: EstadoCondicion, onChange: (EstadoCondicion) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f))
        SegmentedEstado(value) { onChange(it) }
    }
}

@Composable
private fun SegmentedEstado(value: EstadoCondicion, onSelected: (EstadoCondicion) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(EstadoCondicion.SI, EstadoCondicion.NO, EstadoCondicion.ROTO).forEach { estado ->
            FilterChip(
                selected = value == estado,
                onClick = { onSelected(estado) },
                label = { Text(estado.name) }
            )
        }
    }
}
