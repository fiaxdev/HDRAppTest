package com.fiax.hdr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.data.repository.PatientRepository
import com.fiax.hdr.ui.utils.UiEvent
import com.fiax.hdr.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val bluetoothCustomManager: BluetoothCustomManager
): ViewModel() {

    // -------------------Toasts--------------------------------
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private fun sendToast(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowToast(message))
        }
    }
    // ----------------------Patients-------------------------------
    private val _patients = MutableStateFlow<Resource<List<Patient>>>(Resource.None())
    val patients: StateFlow<Resource<List<Patient>>> = _patients

    init {
        loadPatients()
    }

    private fun loadPatients() {
        // Use viewModelScope to launch the coroutine for collecting data
        viewModelScope.launch {
            patientRepository.getPatients()
                .collect { patientsList ->
                   _patients.value = patientsList
                }
        }
    }

    private fun sendPatient(patient: Patient) {
        TODO()
        // bluetoothCustomManager.sendPatient(patient)
    }

//    private fun startListeningForPatients(socket: BluetoothSocket) {
//        viewModelScope.launch(Dispatchers.IO) {
//            bluetoothCustomManager.listenForPatient(
//                socket = socket,
//                onConnectionLost = {toastMessage ->
//                    updateConnectionStatus("Not connected")
//                    updateServerStatus(false)
//                    sendToast(toastMessage)
//                },
//                onPatientReceived = { patient ->
//                    viewModelScope.launch {
//                        _receivedMessages.value += patient  // Update UI
//                    }
//                }
//            )
//        }
//    }

    //-------------------Mock----------------------------------------------------------------------------------

    private fun mockLoadPatients() {
        // Use viewModelScope to launch the coroutine for collecting data
        _patients.value = Resource.Error("Could not load patients")
    }
}