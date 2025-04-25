package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fiax.hdr.data.model.Patient

@Composable
fun PatientItem(patient: Patient){
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
    ){
        Row(modifier = Modifier.fillMaxWidth()){
            Text(patient.name)
            Text(patient.age.toString())
        }
    }
}