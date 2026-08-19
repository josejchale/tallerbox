package com.tallerbox.app.screens.outlet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tallerbox.app.model.usuario.UsuarioEntity
import com.tallerbox.app.navigation.drawerSections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideMenu(
    navController: NavController,
    drawerState: DrawerState,
    usuario: UsuarioEntity?,
    scope: CoroutineScope
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    ModalDrawerSheet(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .verticalScroll(rememberScrollState())
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
                text = if (usuario?.nombre.isNullOrBlank()) "SuperAdmin" else usuario.nombre,
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        drawerSections.forEach { section ->
            Text(
                text = section.title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )

            section.items.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    icon = { Icon(item.icon, contentDescription = null) },
                    onClick = {
                        navController.navigate(item.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}