package com.tallerbox.app.screens.outlet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.usuario.UsuarioEntity
import com.tallerbox.app.navigation.drawerDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.tallerbox.app.repository.usuario.UsuarioRepository
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModel
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModelFactory

@Composable
fun SideMenu(
    navController: NavController,
    drawerState: DrawerState,
    usuario: UsuarioEntity?,
    scope: CoroutineScope
) {

    ModalDrawerSheet(
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.navigate("usuario_perfil")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Editar perfil",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (usuario?.nombre.isNullOrBlank()) "SuperAdmin" else usuario!!.nombre,
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        drawerDestinations.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                icon = { Icon(item.icon, contentDescription = null) },
                onClick = {
                    navController.navigate(item.route)
                    scope.launch { drawerState.close() }
                }
            )
        }
    }
}