package com.tallerbox.app.screens.outlet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.usuario.UsuarioRepository
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModel
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.tallerbox.app.model.usuario.UsuarioEntity
import com.tallerbox.app.navigation.AppNavigation

@Composable
fun RootScreen(navController: NavHostController = rememberNavController()) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
        navController = navController,
        content = { innerPadding: PaddingValues ->
            AppNavigation(navController = navController)
        }
    )
}
