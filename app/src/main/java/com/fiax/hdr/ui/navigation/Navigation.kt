package com.fiax.hdr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiax.hdr.R
import com.fiax.hdr.ui.screens.AddPatientScreen
import com.fiax.hdr.ui.screens.BluetoothScreen
import com.fiax.hdr.ui.screens.HomeScreen
import com.fiax.hdr.viewmodel.BluetoothViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddPatient : Screen("add_patient")
    data object Bluetooth : Screen("bluetooth")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    bluetoothViewModel: BluetoothViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = stringResource(R.string.home), modifier = modifier) {

        composable(Screen.Home.route){ HomeScreen(bluetoothViewModel, navController) }

        composable(Screen.AddPatient.route){ AddPatientScreen(navController) }

        composable(Screen.Bluetooth.route){ BluetoothScreen(navController) }
    }

}


