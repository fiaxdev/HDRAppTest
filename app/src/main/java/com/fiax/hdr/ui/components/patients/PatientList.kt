package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.fiax.hdr.data.model.Patient

@Composable
fun PatientList(patients: List<Patient>) {
    LazyColumn {
        items(patients) { patient ->
            PatientItem(patient)
        }
    }
}