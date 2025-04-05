package com.fiax.hdr.ui.components.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.navigation.AppNavigation
import com.fiax.hdr.viewmodel.BluetoothViewModel
import com.fiax.hdr.viewmodel.PatientViewModel

@Composable
fun MainScaffold(
    navController: NavHostController,
    bluetoothViewModel: BluetoothViewModel,
    patientViewModel: PatientViewModel
) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        AppNavigation(navController, bluetoothViewModel, patientViewModel, Modifier.padding(padding))
    }
}

