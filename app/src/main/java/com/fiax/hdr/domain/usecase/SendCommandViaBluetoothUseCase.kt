package com.fiax.hdr.domain.usecase

import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.bluetooth.BluetoothEnvelope
import com.fiax.hdr.utils.Resource
import kotlinx.serialization.json.Json

class SendCommandViaBluetoothUseCase(
    private val bluetoothManager: BluetoothCustomManager,
) {
    operator fun invoke(command: String): Resource<Unit> {
        val commandBytes = command.toByteArray(Charsets.UTF_8)
        val envelope = BluetoothEnvelope(
            type = "code",
            payload = commandBytes
        )
        val jsonEnvelope = Json.encodeToString(BluetoothEnvelope.serializer(), envelope)
        bluetoothManager.sendData(jsonEnvelope)
        return Resource.Success(Unit)
    }
}