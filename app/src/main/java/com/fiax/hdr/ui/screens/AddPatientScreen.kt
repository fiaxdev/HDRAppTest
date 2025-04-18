package com.fiax.hdr.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.components.patients.PatientForm
import com.fiax.hdr.ui.components.util.TitleText
import com.fiax.hdr.ui.utils.CustomCircularProgressIndicator
import com.fiax.hdr.utils.Resource
import com.fiax.hdr.viewmodel.PatientFormViewModel
import kotlinx.coroutines.launch

@Composable
fun AddPatientScreen(
    navController: NavHostController,
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val patientFormViewModel: PatientFormViewModel = hiltViewModel()
    val insertStatus by patientFormViewModel.insertStatus.collectAsState()
    val toastMessage by patientFormViewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            toastMessage.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                patientFormViewModel.onToastShown() // Reset after showing
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
                onCancel = {
                    scope.launch {
                        navController.popBackStack()
                    }
                },
                patientFormViewModel = patientFormViewModel
            )
        }
        when (insertStatus) {
            is Resource.Loading -> CustomCircularProgressIndicator()
            is Resource.Success -> { navController.popBackStack() }
            is Resource.Error -> {}
            else -> {}
        }
    }

}