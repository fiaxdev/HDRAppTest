package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.ui.components.util.FadeOverlay
import com.fiax.hdr.ui.components.util.SmallGrayText

@Composable
fun PatientList(patients: List<Patient>, navController: NavController) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ){
        SmallGrayText(
            text = "Patient List",
            modifier = Modifier.align(Alignment.Start)
        )

        Box{
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 2.dp),
                content = {
                    items(patients) { patient ->
                        PatientItem(patient, navController)
                    }
                    item { Spacer(Modifier.height(64.dp)) }
                }
            )

            FadeOverlay(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}