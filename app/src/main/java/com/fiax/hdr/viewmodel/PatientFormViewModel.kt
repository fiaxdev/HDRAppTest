package com.fiax.hdr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
import com.fiax.hdr.data.model.FormErrors
import com.fiax.hdr.data.model.FormState
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.data.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientFormViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
): ViewModel(){

    private val appContext = HDRApp.getAppContext()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState

    private val _errors = MutableStateFlow(FormErrors())
    val errors: StateFlow<FormErrors> = _errors

    private val _insertStatus = MutableStateFlow<Resource<Unit>>(Resource.None())
    val insertStatus: StateFlow<Resource<Unit>> = _insertStatus

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    // Reset toast message after showing
    fun onToastShown() {
        _toastMessage.value = ""
    }

    private fun updateToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name)
    }

    fun updateAge(age: String) {
        _formState.value = _formState.value.copy(age = age)
    }

    fun updateSex(sex: String) {
        _formState.value = _formState.value.copy(sex = sex)
    }

    fun updateVillage(village: String) {
        _formState.value = _formState.value.copy(village = village)
    }

    fun updateParish(parish: String) {
        _formState.value = _formState.value.copy(parish = parish)
    }

    fun updateSubCounty(subCounty: String) {
        _formState.value = _formState.value.copy(subCounty = subCounty)
    }

    fun updateDistrict(district: String) {
        _formState.value = _formState.value.copy(district = district)
    }

    fun updateNextOfKin(nextOfKin: String) {
        _formState.value = _formState.value.copy(nextOfKin = nextOfKin)
    }

    fun updateContact(contact: String) {
        _formState.value = _formState.value.copy(contact = contact)
    }

    private fun validate(): Boolean {
        val state = _formState.value
        var isValid = true

        if (state.name.isBlank()) {
            isValid = false
            _errors.value = _errors.value.copy(nameError = "Name is required")
        } else {
            _errors.value = _errors.value.copy(nameError = null)
        }

        if (state.age.toIntOrNull() == null || state.age.toInt() <= 0) {
            isValid = false
            _errors.value = _errors.value.copy(ageError = "Enter a valid age")
        } else {
            _errors.value = _errors.value.copy(ageError = null)
        }

//        if (state.sex.isBlank() or state.sex.isEmpty()) {
//            isValid = false
//            _errors.value = _errors.value.copy(sexError = "Choose a sex")
//        } else {
//            _errors.value = _errors.value.copy(sexError = null)
//        }
//
//        if (state.village.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(villageError = "Village is required")
//        } else {
//            _errors.value = _errors.value.copy(villageError = null)
//        }
//
//        if (state.parish.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(parishError = "Parish is required")
//        } else {
//            _errors.value = _errors.value.copy(parishError = null)
//        }
//
//        if (state.subCounty.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(subCountyError = "Sub-county is required")
//        } else {
//            _errors.value = _errors.value.copy(subCountyError = null)
//        }
//
//        if (state.district.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(districtError = "District is required")
//        } else {
//            _errors.value = _errors.value.copy(districtError = null)
//        }
//
//        if (state.nextOfKin.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(nextOfKinError = "Next of kin is required")
//        } else {
//            _errors.value = _errors.value.copy(nextOfKinError = null)
//        }
//
//        if (state.contact.isBlank()) {
//            isValid = false
//            _errors.value = _errors.value.copy(contactError = "Contact is required")
//        } else {
//            _errors.value = _errors.value.copy(contactError = null)
//        }

        return isValid
    }

    fun submitForm() {
        val valid = validate()
        if (valid) {
            // send form to server or save locally
            val patient = Patient(
                name = _formState.value.name,
                age = _formState.value.age.toInt(),
                sex = _formState.value.sex,
                village = _formState.value.village,
                parish = _formState.value.parish,
                subCounty = _formState.value.subCounty,
                district = _formState.value.district,
                nextOfKin = _formState.value.nextOfKin,
                contact = _formState.value.contact
            )
            viewModelScope.launch {
                addPatient(patient)
            }
        } else
            updateToastMessage(appContext.getString(R.string.add_patient_form_not_valid))

    }

    private suspend fun addPatient(patient: Patient) {
        updateInsertionStatus(Resource.Loading())
        delay(3000) // Simulate network delay, to remove in the future
        val result = patientRepository.addPatient(patient)
        updateInsertionStatus(result)
        when (result) {
            is Resource.Success -> {
                updateToastMessage(appContext.getString(R.string.add_patient_success))
            }
            is Resource.Error -> {
                updateToastMessage(appContext.getString(R.string.add_patient_error))
            }
            else -> {}
        }
    }

    private fun updateInsertionStatus(status: Resource<Unit>){
        _insertStatus.value = status
    }
}