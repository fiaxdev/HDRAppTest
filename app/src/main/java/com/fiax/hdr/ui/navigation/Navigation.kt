package com.fiax.hdr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiax.hdr.R
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.ui.screens.AddPatientScreen
import com.fiax.hdr.ui.screens.HomeScreen
import com.fiax.hdr.ui.screens.PairNewDeviceScreen
import com.fiax.hdr.ui.screens.PatientDetailsScreen
import com.fiax.hdr.ui.screens.SendPatientViaBluetoothScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddPatient : Screen("add_patient")
    data object SendPatient : Screen("send_patient")
    data object PairDevice : Screen("pair_device")
    data object PatientDetails : Screen("patient_details")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = stringResource(R.string.nav_home),
        modifier = modifier
    ) {

        composable(Screen.Home.route){ HomeScreen(navController) }

        composable(Screen.AddPatient.route){ AddPatientScreen(navController) }

        composable(Screen.SendPatient.route){
            // Get the patient from SavedStateHandle
            val patient = navController
                .previousBackStackEntry
            ?.savedStateHandle
            ?.get<Patient>("patient")

            if (patient != null) {
                SendPatientViaBluetoothScreen(
                    patient = patient,
                    navController = navController
                )
            }
        }

        composable(Screen.PairDevice.route){ PairNewDeviceScreen(navController) }

        composable(Screen.PatientDetails.route){
            // Get the patient from SavedStateHandle
            val patient = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Patient>("patient")

            if (patient != null) {
                PatientDetailsScreen(
                    patient = patient,
                    navController = navController
                )
            }
        }
    }
}




