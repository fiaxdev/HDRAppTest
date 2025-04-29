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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fiax.hdr.ui.components.util.SmallGrayText

@Composable
fun DeviceList(
    onClick: (BluetoothDevice) -> Unit,
    pairedDevices: List<BluetoothDevice>,
    discoveredDevices: List<BluetoothDevice>,
    connectionSocket: BluetoothSocket?,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            DeviceListBuilder(
                pairedDevices,
                "Paired Devices",
                connectionSocket,
                onClick
            )
        }

        item {
            DeviceListBuilder(
                discoveredDevices,
                "Discovered Devices",
                connectionSocket,
                onClick
            )
        }
    }
    }


@Composable
private fun DeviceListBuilder(
    devices: List<BluetoothDevice>,
    devicesType: String,
    connectionSocket: BluetoothSocket?,
    onClick: (BluetoothDevice) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SmallGrayText(
            devicesType,
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
                        connectionSocket = connectionSocket,
                        //connectionSocket = connectionSocket,
                        onClick = { onClick(device) }
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