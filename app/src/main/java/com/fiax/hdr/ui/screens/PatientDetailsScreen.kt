package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fiax.hdr.data.model.Patient

@Composable
fun PatientDetailsScreen(patient: Patient, navController: NavHostController) {

    Column {
        Text("ID: ${patient.id}")
        Text("Name: ${patient.name}")
        Text("Surname: ${patient.age}")
        Text("Address: ${patient.sex}")
        Text("City: ${patient.village}")
        Text("Phone: ${patient.parish}")
        Text("Email: ${patient.subCounty}")
        Text("Birth Date: ${patient.district}")
        Text("Gender: ${patient.nextOfKin}")
        Text("Gender: ${patient.contact}")
    }

}