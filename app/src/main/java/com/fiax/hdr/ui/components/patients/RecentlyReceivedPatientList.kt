package com.fiax.hdr.ui.components.patients

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.fiax.hdr.data.model.Patient

@Composable
fun RecentlyReceivedPatientList(
    patients: List<Patient>?,
    navController: NavController
){
    if (patients != null) {
        PatientList(
            patients = patients,
            navController = navController,
            title = "Recently Received Patients"
        )
    } else
        Log.d("RecentlyReceivedPatientList", "Patients are null")
}