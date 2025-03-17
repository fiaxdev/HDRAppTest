package com.fiax.hdr.ui.components.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.navigation.AppNavigation
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel
import com.fiax.hdr.ui.viewmodel.NfcViewModel

@Composable
fun MainScaffold(
    navController: NavHostController,
    nfcViewModel: NfcViewModel,
    bluetoothViewModel: BluetoothViewModel,
) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        AppNavigation(navController, nfcViewModel, bluetoothViewModel, Modifier.padding(padding))
    }
}

