package com.fiax.hdr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.ui.components.bluetooth.devices.DeviceList
import com.fiax.hdr.ui.components.patients.PatientItem
import com.fiax.hdr.ui.components.util.BottomButtons
import com.fiax.hdr.ui.components.util.CenteredTextInBox
import com.fiax.hdr.ui.components.util.FadeOverlay
import com.fiax.hdr.ui.components.util.TitleText
import com.fiax.hdr.ui.components.util.circularprogressindicator.CustomCircularProgressIndicator
import com.fiax.hdr.ui.navigation.Screen
import com.fiax.hdr.utils.Resource
import com.fiax.hdr.viewmodel.SendPatientViaBluetoothViewModel


@Composable
fun SendPatientViaBluetoothScreen(
    patient: Patient,
    navController: NavController
) {

    val sendPatientViaBluetoothViewModel: SendPatientViaBluetoothViewModel = hiltViewModel()

    val pairedDevices = sendPatientViaBluetoothViewModel.pairedDevices.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Patient:")

                    PatientItem(
                        patient = patient,
                        navController = navController,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        hideBluetoothButton = true,
                        onClick = { navController.popBackStack() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(4.5f)
                ) {
                    TitleText("Select the receiver", Modifier.padding(horizontal = 16.dp))

                    DeviceList(
                        devices = pairedDevices.value,
                        devicesType = "Paired Devices",
                        onClick = {
                            sendPatientViaBluetoothViewModel.sendPatient(patient, it)
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { navController.navigate(Screen.PairDevice.route) }
                    ){ Text("Need to pair a new device?") }
                }
            }

            FadeOverlay(Modifier.align(Alignment.BottomCenter))
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        ){

            Box (
                modifier = Modifier.weight(1f)
            ){
                sendPatientViaBluetoothViewModel.sendPatientResult.collectAsState().value.let {
                    when (it) {
                        is Resource.Success -> CenteredTextInBox("Patient sent successfully, tap another device to send the same patient to that device")
                        is Resource.Error -> CenteredTextInBox(it.message ?: "Error sending patient")
                        is Resource.Loading<*> -> CustomCircularProgressIndicator()
                        is Resource.None<*> -> {}
                    }
                }
            }

            BottomButtons(
                onCancel = { navController.popBackStack() },
                onConfirm = { navController.popBackStack() },
                confirmButtonText = "Done"
            )
        }
    }
}
