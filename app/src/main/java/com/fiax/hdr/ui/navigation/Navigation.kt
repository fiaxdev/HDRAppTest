package com.fiax.hdr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiax.hdr.R
import com.fiax.hdr.ui.screens.HomeScreen
import com.fiax.hdr.viewmodel.BluetoothViewModel
import com.fiax.hdr.viewmodel.PatientViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    bluetoothViewModel: BluetoothViewModel,
    patientViewModel: PatientViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = stringResource(R.string.home), modifier = modifier) {
        composable(Screen.Home.route){ HomeScreen(bluetoothViewModel, patientViewModel) }
    }
}


