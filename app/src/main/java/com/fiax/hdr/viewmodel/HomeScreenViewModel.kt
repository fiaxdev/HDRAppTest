package com.fiax.hdr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    patientRepository: PatientRepository,
): ViewModel() {

    // ----------------------Patients-------------------------------

    val patients: StateFlow<Resource<List<Patient>>> = patientRepository
        .getPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading())


    val receivedPatients: StateFlow<List<Patient>> = patientRepository
        .receivedPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // ---------------------Enable bluetooth-------------------------------

//    init {
//        loadPatients()
//    }
//
//    private fun loadPatients() {
//        // Use viewModelScope to launch the coroutine for collecting data
//        viewModelScope.launch {
//            patientRepository.getPatients()
//                .collect { patientsList ->
//                   _patients.value = patientsList
//                }
//        }
//    }

    //-------------------Mock----------------------------------------------------------------------------------

//    private fun mockLoadPatients() {
//        // Use viewModelScope to launch the coroutine for collecting data
//        _patients.value = Resource.Error("Could not load patients")
//    }
}