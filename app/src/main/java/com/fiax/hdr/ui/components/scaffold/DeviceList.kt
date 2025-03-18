package com.fiax.hdr.ui.components.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel

@Composable
fun DeviceList(bluetoothViewModel: BluetoothViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val devices by bluetoothViewModel.discoveredDevices.collectAsState()
    val connectionStatus by bluetoothViewModel.connectionStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = connectionStatus, style = MaterialTheme.typography.bodyLarge)

        LazyColumn {
            items(devices.size) { index ->
                val device = devices[index]
                DeviceItem(
                    device = device,
                    onClick = {bluetoothViewModel.connectToDevice(device)}
                )
            }
        }
    }
}