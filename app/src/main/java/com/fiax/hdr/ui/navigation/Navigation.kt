package com.fiax.hdr.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiax.hdr.R
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.ui.screens.AddPatientScreen
import com.fiax.hdr.ui.screens.BluetoothScreen
import com.fiax.hdr.ui.screens.HomeScreen
import com.fiax.hdr.ui.screens.SendPatientViaBluetoothScreen

sealed class Screen(val route: String, @StringRes val routeId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object AddPatient : Screen("add_patient", R.string.nav_add_patient)
    data object Bluetooth : Screen("bluetooth", R.string.nav_bluetooth)
    data object SendPatient : Screen("send_patient", R.string.nav_send_patient)
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = stringResource(R.string.nav_home), modifier = modifier) {

        composable(Screen.Home.route){ HomeScreen(navController) }

        composable(Screen.AddPatient.route){ AddPatientScreen(navController) }

        composable(Screen.Bluetooth.route){ BluetoothScreen(navController) }

        composable(Screen.SendPatient.route){
            // Get the patient from SavedStateHandle
            val patient = navController
                .previousBackStackEntry
            ?.savedStateHandle
            ?.get<Patient>("patient")

            if (patient != null) {
                SendPatientViaBluetoothScreen(
                    patient = patient,
                    navHostController = navController
                )
            }
        }

    }
}


