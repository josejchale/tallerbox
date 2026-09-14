package com.tallerbox.app.screens.outlet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout(
    drawerState: DrawerState,
    sideMenu: @Composable () -> Unit,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { sideMenu() }
    ) {
        Scaffold(
            topBar = {
                TopBar(drawerState, scope, navController)
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
