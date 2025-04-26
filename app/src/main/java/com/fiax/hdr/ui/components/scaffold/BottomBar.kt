package com.fiax.hdr.ui.components.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.fiax.hdr.R

@Composable
fun BottomBar(navController: NavHostController) {

    // Routes
    val homeRoute = stringResource(R.string.nav_home)
    val bluetoothRoute = stringResource(R.string.nav_bluetooth)

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == homeRoute,
            onClick = { navController.navigate(homeRoute) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth") },
            label = { Text("Bluetooth") },
            selected = navController.currentDestination?.route == bluetoothRoute,
            onClick = { navController.navigate(bluetoothRoute) }
        )
    }
}