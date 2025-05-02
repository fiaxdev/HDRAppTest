package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fiax.hdr.R
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.ui.components.util.TitleText

@Composable
fun PatientItem(
    patient: Patient,
    navController: NavController,
    modifier: Modifier = Modifier,
    hideBluetoothButton: Boolean = false,
    onClick: () -> Unit = {}
){
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp)
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                TitleText(patient.name, color = MaterialTheme.colorScheme.onSurface)
                Text(patient.age.toString(), color = MaterialTheme.colorScheme.onSurface)
            }

            if (!hideBluetoothButton)
                BluetoothButton(patient, navController)
        }
    }
}

@Composable
fun BluetoothButton(patient: Patient, navController: NavController) {

    val route = stringResource(R.string.nav_send_patient)

    IconButton(
        onClick = {
            navController.currentBackStackEntry?.savedStateHandle?.set("patient", patient)
            navController.navigate(route)
        },
    ) {
        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = "Send Patient via Bluetooth",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
