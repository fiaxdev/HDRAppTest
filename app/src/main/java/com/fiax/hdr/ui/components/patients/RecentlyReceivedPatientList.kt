package com.fiax.hdr.ui.components.patients

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavController
import com.fiax.hdr.data.model.Patient

@Composable
fun RecentlyReceivedPatientList(
    patient: State<Patient?>,
    navController: NavController
){
    if (patient.value != null) {
        PatientList(
            patients = listOf(patient.value!!),
            navController = navController,
            title = "Recently Received Patients"
        )
    }
}