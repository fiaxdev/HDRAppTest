package com.fiax.hdr.domain.usecase

import android.bluetooth.BluetoothDevice
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.utils.Resource

class SendPatientViaBluetoothUseCase(
    private val bluetoothManager: BluetoothCustomManager,
) {
    suspend operator fun invoke(patient: Patient, device: BluetoothDevice): Resource<Unit> {
        return try {
            val socket = bluetoothManager.connectToServer(device)
                ?: return Resource.Error("Failed to connect to the device")

            bluetoothManager.sendPatient(socket, patient)
            bluetoothManager.disconnect()
            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
