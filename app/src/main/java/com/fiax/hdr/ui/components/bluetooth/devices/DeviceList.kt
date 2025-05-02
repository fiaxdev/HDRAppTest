package com.fiax.hdr.ui.components.bluetooth.devices

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fiax.hdr.ui.components.util.SmallGrayText
import com.fiax.hdr.ui.components.util.circularprogressindicator.SmallCircularProgressIndicator

@Composable
fun DeviceList(
    onClick: (BluetoothDevice) -> Unit,
    pairedDevices: List<BluetoothDevice>,
    discoveredDevices: List<BluetoothDevice>,
    connectionSocket: BluetoothSocket?,
    modifier: Modifier = Modifier,
    onScanButtonClick: () -> Unit = {},
    isDiscovering: Boolean = false
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
                onClick,
                true,
                onScanButtonClick,
                isDiscovering
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
private fun DeviceListBuilder(
    devices: List<BluetoothDevice>,
    devicesType: String,
    connectionSocket: BluetoothSocket?,
    onClick: (BluetoothDevice) -> Unit,
    showScanButton: Boolean = false,
    onScanButtonClick: () -> Unit = {},
    isDiscovering: Boolean = false
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            SmallGrayText(devicesType)

            Row (verticalAlignment = Alignment.CenterVertically) {

                if (isDiscovering)
                    SmallCircularProgressIndicator()

                if (showScanButton) {
                    TextButton(
                        onClick = { onScanButtonClick() },
                    ) { Text(if (!isDiscovering) "Scan" else "Stop") }
                }
            }
        }
        Column (
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ){
            if (devices.isNotEmpty()){
                for (device in devices) {
                    DeviceItem(
                        device = device,
                        connectionSocket = connectionSocket,
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