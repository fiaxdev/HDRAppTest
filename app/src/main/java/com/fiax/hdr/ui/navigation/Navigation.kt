package com.fiax.hdr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiax.hdr.R
import com.fiax.hdr.ui.screens.HomeScreen
import com.fiax.hdr.ui.screens.NfcScreen
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel
import com.fiax.hdr.ui.viewmodel.NfcViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object NFC : Screen("nfc")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    nfcViewModel: NfcViewModel,
    bluetoothViewModel: BluetoothViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = stringResource(R.string.home), modifier = modifier) {
        composable(Screen.Home.route){ HomeScreen(bluetoothViewModel) }
        composable(Screen.NFC.route) { NfcScreen(nfcViewModel) }
    }
}


