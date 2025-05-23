package com.fiax.hdr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.data.model.FormErrors
import com.fiax.hdr.data.model.FormState
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPatientScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
): ViewModel(){

    // -------------------Toasts--------------------------------
//    private val _uiEvent = MutableSharedFlow<UiEvent>()
//    val uiEvent = _uiEvent.asSharedFlow()
//
//    private fun sendToast(message: String) {
//        viewModelScope.launch {
//            _uiEvent.emit(UiEvent.ShowToast(message))
//        }
//    }

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState

    private val _errors = MutableStateFlow(FormErrors())
    val errors: StateFlow<FormErrors> = _errors

    private val _insertStatus = MutableStateFlow<Resource<Unit>>(Resource.None())
    val insertStatus: StateFlow<Resource<Unit>> = _insertStatus

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
        } //else
//            sendToast(appContext.getString(R.string.add_patient_form_not_valid))
    }

    private suspend fun addPatient(patient: Patient) {
        updateInsertionStatus(Resource.Loading())
        delay(1000) // Simulate network delay, to remove in the future
        val result = patientRepository.addPatient(patient)
        updateInsertionStatus(result)
        when (result) {
            is Resource.Success -> {
//                sendToast(appContext.getString(R.string.add_patient_success))
            }
            is Resource.Error -> {
//                sendToast(appContext.getString(R.string.add_patient_error))
            }
            else -> {}
        }
    }

    private fun updateInsertionStatus(status: Resource<Unit>){
        _insertStatus.value = status
    }
}