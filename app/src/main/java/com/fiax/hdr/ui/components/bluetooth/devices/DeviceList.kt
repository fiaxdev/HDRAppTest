package com.fiax.hdr.ui.components.bluetooth.devices

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fiax.hdr.viewmodel.BluetoothViewModel

@Composable
fun DeviceList(bluetoothViewModel: BluetoothViewModel) {

    val pairedDevices by bluetoothViewModel.pairedDevices.collectAsState()
    val discoveredDevices by bluetoothViewModel.discoveredDevices.collectAsState()
    val connectionSocket by bluetoothViewModel.connectionSocket.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            DeviceListBuilder(
                pairedDevices,
                "Paired Devices",
                connectionSocket,
                bluetoothViewModel
            )
        }

        item {
            DeviceListBuilder(
                discoveredDevices,
                "Discovered Devices",
                connectionSocket,
                bluetoothViewModel
            )
        }
    }
    }


@Composable
private fun DeviceListBuilder(
    devices: List<BluetoothDevice>,
    devicesType: String,
    connectionSocket: BluetoothSocket?,
    bluetoothViewModel: BluetoothViewModel
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = devicesType,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Start)
        )
        Column (
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ){
            if (devices.isNotEmpty()){
                for (device in devices) {
                    DeviceItem(
                        device = device,
                        onClick = {
                            if (device == connectionSocket?.remoteDevice)
                                bluetoothViewModel.disconnect()
                            else
                                bluetoothViewModel.connectToDevice(device)
                        }
                    )
                }
            } else
                Card(modifier = Modifier.fillMaxWidth()){
                    Text(
                        text ="No devices found",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                    )
                }
        }
    }
}