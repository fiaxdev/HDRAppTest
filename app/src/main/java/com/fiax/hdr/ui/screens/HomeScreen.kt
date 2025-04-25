package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.navigation.Screen
import com.fiax.hdr.viewmodel.BluetoothViewModel

@Composable
fun HomeScreen(
    bluetoothViewModel: BluetoothViewModel,
    navController: NavHostController,
) {
    Box {
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.AddPatient.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Patient"
            )
        }
    }
}
