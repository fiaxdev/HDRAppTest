package com.fiax.hdr.domain.usecase

import android.bluetooth.BluetoothDevice
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.bluetooth.BluetoothEnvelope
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.model.PatientSerializer
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SendPatientViaBluetoothUseCase(
    private val bluetoothManager: BluetoothCustomManager,
) {
    suspend operator fun invoke(patient: Patient, device: BluetoothDevice): Resource<Unit> {
        return try {
            var enabled = false
            var result: Resource<Unit> = Resource.Loading()
            withContext(Dispatchers.IO){
                bluetoothManager.ensureBluetoothEnabled(
                    onEnabled = {
                        enabled = true
                    }
                )
                if (enabled){
                    try {
                        if (bluetoothManager.connectToServer(device) != null){
                            val patientBytes = PatientSerializer.serialize(patient)
                            val envelope = BluetoothEnvelope(
                                type = "data",
                                payload = patientBytes
                            )
                            val jsonEnvelope = Json.encodeToString(BluetoothEnvelope.serializer(), envelope)
                            bluetoothManager.sendData(jsonEnvelope)
                            delay(1000)
                            bluetoothManager.disconnect()
                            result = Resource.Success(Unit)
                        } else
                            result = Resource.Error("Could not connect to device")

                    } catch (e: Exception) {
                        result = Resource.Error(e.message ?: "An error occurred")
                    }
                }
            }
            result

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
