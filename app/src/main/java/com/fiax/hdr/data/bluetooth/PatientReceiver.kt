package com.fiax.hdr.data.bluetooth

import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.usecase.InsertPatientLocallyUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientReceiver @Inject constructor(
    bluetoothManager: BluetoothCustomManager,
    private val insertPatientUseCase: InsertPatientLocallyUseCase
) {

    private val receiverScope = CoroutineScope(Dispatchers.IO)

    init {
        receiverScope.launch {
            bluetoothManager.receivedPatients.collect { patient: Patient ->
                insertPatientUseCase(patient)
            }
        }
    }
}
