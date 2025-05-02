package com.fiax.hdr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
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
    bluetoothCustomManager: BluetoothCustomManager,
): ViewModel() {

    // -------------------Toasts--------------------------------
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun sendToast() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowToast(uiText.value))
        }
    }

    fun sendSnackBar() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar(uiText.value))
        }
    }
    // ----------------------Patients-------------------------------
    private val _patients = MutableStateFlow<Resource<List<Patient>>>(Resource.None())
    val patients: StateFlow<Resource<List<Patient>>> = _patients

    val receivedPatients = patientRepository.receivedPatients

    // ---------------------Enable bluetooth--------------------------------
    val enablerResult = bluetoothCustomManager.enablingResult

    private val _uiText = MutableStateFlow("")
    val uiText: StateFlow<String> = _uiText

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

    fun setUiText(text: String){
        _uiText.value = text
    }

    fun clearUiText(){
        setUiText("")
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