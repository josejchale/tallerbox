package com.tallerbox.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.usuario.UsuarioRepository
import com.tallerbox.app.screens.outlet.SideMenu
import com.tallerbox.app.screens.outlet.AppLayout
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModel
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val usuarioRepo = UsuarioRepository(db.usuarioDao())
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(usuarioRepo))
    val usuario by usuarioViewModel.usuario.collectAsState()

    AppLayout(
        drawerState = drawerState,
        sideMenu = {
            SideMenu(navController, drawerState, usuario, scope)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
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
                }
            }
        }
    )
}