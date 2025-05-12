package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.ui.components.patients.PatientList
import com.fiax.hdr.ui.components.patients.RecentlyReceivedPatientList
import com.fiax.hdr.ui.components.util.GenericErrorBoxAndText
import com.fiax.hdr.ui.components.util.circularprogressindicator.CustomCircularProgressIndicator
import com.fiax.hdr.ui.navigation.Screen
import com.fiax.hdr.utils.Resource
import com.fiax.hdr.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
) {

    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()

    val patients = homeScreenViewModel.patients.collectAsState()

    val receivedPatients = homeScreenViewModel.receivedPatients.collectAsState(null)

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RecentlyReceivedPatientList(receivedPatients.value, navController)

            when (patients.value) {
                is Resource.Error<*> -> {
                    GenericErrorBoxAndText((patients.value as Resource.Error).message)
                }

                is Resource.Loading<*> -> CustomCircularProgressIndicator()
                is Resource.None<*> -> {}
                is Resource.Success<*> -> PatientList(
                    patients.value.data as List<Patient>,
                    navController
                )
            }
        }

        AddPatientFAB(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { navController.navigate(Screen.AddPatient.route) }
        )
    }
}

@Composable
fun AddPatientFAB(
    modifier: Modifier,
    onClick: () -> Unit,
){
    FloatingActionButton(
        onClick = { onClick() },
        modifier = modifier.padding(16.dp),
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Patient"
        )
    }
}

