package com.fiax.hdr.ui.components.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.fiax.hdr.data.model.Patient
import kotlinx.coroutines.launch

@Composable
fun PatientForm(
    onSubmit: (patient: Patient) -> Unit,

){

    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember { mutableStateOf(TextFieldValue("")) }
    var sex by remember { mutableStateOf(TextFieldValue("")) }
    var village by remember { mutableStateOf(TextFieldValue("")) }
    var parish by remember { mutableStateOf(TextFieldValue("")) }
    var subCounty by remember { mutableStateOf(TextFieldValue("")) }
    var district by remember { mutableStateOf(TextFieldValue("")) }
    var nextOfKin by remember { mutableStateOf(TextFieldValue("")) }
    var contact by remember { mutableStateOf(TextFieldValue("")) }

    // Error variables
    var nameError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    var sexError by remember { mutableStateOf(false) }
    var villageError by remember { mutableStateOf(false) }
    var parishError by remember { mutableStateOf(false) }
    var subCountyError by remember { mutableStateOf(false) }
    var districtError by remember { mutableStateOf(false) }
    var nextOfKinError by remember { mutableStateOf(false) }
    var contactError by remember { mutableStateOf(false) }

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){

        item {
            OutlinedTextField(
                value = name,
                isError = nameError,
                onValueChange = {
                    name = it
                    nameError = !isValid(name.text)
                },
                label = { Text("Name") }
            )
        }

        item {
            OutlinedTextField(
                value = age,
                onValueChange = {age = it},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = ageError,
                label = { Text("Age") }
            )
        }

        item {
            OutlinedTextField(
                value = sex,
                isError = sexError,
                onValueChange = {
                    sex = it
                    sexError = !isValid(sex.text)
                },
                label = { Text("Sex") }
            )
        }

        item {
            OutlinedTextField(
                value = village,
                isError = villageError,
                onValueChange = {
                    village = it
                    villageError = !isValid(village.text)
                },
                label = { Text("Village") }
            )
        }

        item {
            OutlinedTextField(
                value = parish,
                isError = parishError,
                onValueChange = {
                    parish = it
                    parishError = !isValid(parish.text)
                },
                label = { Text("Parish") }
            )
        }

        item {
            OutlinedTextField(
                value = subCounty,
                isError = subCountyError,
                onValueChange = {
                    subCounty = it
                    subCountyError = !isValid(subCounty.text)
                },
                label = { Text("Sub-County") }
            )
        }

        item {
            OutlinedTextField(
                value = district,
                isError = districtError,
                onValueChange = { district = it
                    districtError = !isValid(district.text)
                },
                label = { Text("District") }
            )
        }

        item {
            OutlinedTextField(
                value = nextOfKin,
                isError = nextOfKinError,
                onValueChange = {
                    nextOfKin = it
                    nextOfKinError = !isValid(nextOfKin.text)
                },
                label = { Text("Next of Kin") }
            )
        }

        item {
            OutlinedTextField(
                value = contact,
                isError = contactError,
                onValueChange = {
                    contact = it
                    contactError = !isValid(contact.text)
                },
                label = { Text("Contact") }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(onClick = { /*TODO*/ }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val ageInput = age.text.toIntOrNull()
                        ageError = ageInput == null

                        if (!nameError && !sexError && !villageError && !parishError && !subCountyError && !districtError && !nextOfKinError && !contactError && !ageError) {

                            val newPatient = Patient(
                                name = name.text,
                                age = ageInput!!,
                                sex = sex.text,
                                village = village.text,
                                parish = parish.text,
                                subCounty = subCounty.text,
                                district = district.text,
                                nextOfKin = nextOfKin.text,
                                contact = contact.text
                            )
                            scope.launch { onSubmit(newPatient) }
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }

    }
}

fun isValid(input: String):Boolean {
    return input.isNotBlank() and input.isNotEmpty()
}