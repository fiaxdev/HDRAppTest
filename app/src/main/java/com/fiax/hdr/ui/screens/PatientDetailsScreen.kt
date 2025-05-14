package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fiax.hdr.domain.model.Patient

@Composable
fun PatientDetailsScreen(patient: Patient, navController: NavHostController) {

    Column {
        Text("ID: ${patient.id}")
        Text("Name: ${patient.name}")
        Text("Age: ${patient.age}")
        Text("Sex: ${patient.sex}")
        Text("Village: ${patient.village}")
        Text("Parish: ${patient.parish}")
        Text("SubCounty: ${patient.subCounty}")
        Text("District: ${patient.district}")
        Text("Next of Kin: ${patient.nextOfKin}")
        Text("Contact: ${patient.contact}")
    }

}