package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fiax.hdr.viewmodel.PatientFormViewModel

@Composable
fun PatientForm(
    onCancel: () -> Unit,
    patientFormViewModel: PatientFormViewModel
) {

    val errors by patientFormViewModel.errors.collectAsState()
    val formState by patientFormViewModel.formState.collectAsState()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                item {
                    OutlinedTextField(
                        value = formState.name,
                        isError = errors.nameError != null,
                        onValueChange = { patientFormViewModel.updateName(it) },
                        label = { Text("Name") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.age,
                        onValueChange = { patientFormViewModel.updateAge(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = errors.ageError != null,
                        label = { Text("Age") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.sex,
                        isError = errors.sexError != null,
                        onValueChange = { patientFormViewModel.updateSex(it) },
                        label = { Text("Sex") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.village,
                        isError = errors.villageError != null,
                        onValueChange = { patientFormViewModel.updateVillage(it) },
                        label = { Text("Village") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.parish,
                        isError = errors.parishError != null,
                        onValueChange = { patientFormViewModel.updateParish(it) },
                        label = { Text("Parish") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.subCounty,
                        isError = errors.subCountyError != null,
                        onValueChange = { patientFormViewModel.updateSubCounty(it) },
                        label = { Text("Sub-County") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.district,
                        isError = errors.districtError != null,
                        onValueChange = { patientFormViewModel.updateDistrict(it) },
                        label = { Text("District") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.nextOfKin,
                        isError = errors.nextOfKinError != null,
                        onValueChange = { patientFormViewModel.updateNextOfKin(it) },
                        label = { Text("Next of Kin") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = formState.contact,
                        isError = errors.contactError != null,
                        onValueChange = { patientFormViewModel.updateContact(it) },
                        label = { Text("Contact") }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp)) // Adds breathing room before bottom buttons
                }
            }

            // Fade overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    patientFormViewModel.submitForm()
                }
            ) {
                Text("Save")
            }
        }
    }
}