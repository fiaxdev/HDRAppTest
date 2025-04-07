package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.components.patients.PatientForm
import com.fiax.hdr.ui.components.util.TitleText
import com.fiax.hdr.viewmodel.PatientViewModel
import kotlinx.coroutines.launch

@Composable
fun AddPatientScreen(
    patientViewModel: PatientViewModel,
    navController: NavHostController,
){

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            TitleText("Register a new patient")

            Spacer(modifier = Modifier.height(24.dp))

            PatientForm(
                onSubmit = { patient ->
                    scope.launch {
                        patientViewModel.addPatient(patient)
                    }
                    navController.popBackStack()
                }
            )
        }
    }

}