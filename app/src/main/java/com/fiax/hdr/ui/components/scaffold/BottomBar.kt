package com.fiax.hdr.ui.components.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.navigation.Screen

@Composable
fun BottomBar(navController: NavHostController) {

    // Routes

    val homeRoute = Screen.Home.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == homeRoute,
            onClick = {
                if (navController.currentDestination?.route != homeRoute)
                    navController.navigate(homeRoute)
            }
        )
    }
}