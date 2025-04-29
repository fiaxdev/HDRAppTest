package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.ui.components.bluetooth.devices.DeviceList
import com.fiax.hdr.ui.components.util.CustomCircularProgressIndicator
import com.fiax.hdr.utils.Resource
import com.fiax.hdr.viewmodel.SendPatientViaBluetoothViewModel


@Composable
fun SendPatientViaBluetoothScreen(
    patient: Patient,
    navHostController: NavHostController
) {

    val sendPatientViaBluetoothViewModel: SendPatientViaBluetoothViewModel = hiltViewModel()

    val pairedDevices = sendPatientViaBluetoothViewModel.pairedDevices.collectAsState()

    val discoveredDevices = sendPatientViaBluetoothViewModel.discoveredDevices.collectAsState()

    val connectionSocket = sendPatientViaBluetoothViewModel.connectionSocket.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text("Choose the device to send the patient to")

            DeviceList(
                pairedDevices = pairedDevices.value,
                discoveredDevices = discoveredDevices.value,
                connectionSocket = connectionSocket.value,
                onClick = {
                    sendPatientViaBluetoothViewModel.sendPatient(patient, it)
                }
            )

            sendPatientViaBluetoothViewModel.sendPatientResult.collectAsState().value.let {
                when (it) {
                    is Resource.Success -> Text("Patient sent successfully")
                    is Resource.Error -> Text(it.message ?: "Error sending patient")
                    is Resource.Loading<*> -> CustomCircularProgressIndicator()
                    is Resource.None<*> -> {}
                }
            }
        }

        OutlinedButton(
            onClick = { navHostController.popBackStack() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 16.dp)
        ) {
            Text("Cancel")
        }
    }
}
