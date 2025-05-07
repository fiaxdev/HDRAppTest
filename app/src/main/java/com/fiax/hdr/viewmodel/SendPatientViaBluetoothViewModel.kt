package com.fiax.hdr.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.domain.usecase.SendPatientViaBluetoothUseCase
import com.fiax.hdr.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendPatientViaBluetoothViewModel @Inject constructor(
    private val useCase: SendPatientViaBluetoothUseCase,
    bluetoothCustomManager: BluetoothCustomManager
) : ViewModel(){

    val pairedDevices = bluetoothCustomManager.pairedDevices

    private val _sendPatientResult = MutableStateFlow<Resource<Unit>>(Resource.None())
    val sendPatientResult: MutableStateFlow<Resource<Unit>> = _sendPatientResult

    init {
        bluetoothCustomManager.fetchPairedDevices()
    }

    fun sendPatient(patient: Patient, device: BluetoothDevice) {
        viewModelScope.launch {
            _sendPatientResult.value = Resource.Loading()
            _sendPatientResult.value = useCase(patient, device)
        }
    }
}