package com.fiax.hdr.ui.screens

import android.Manifest
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(bluetoothViewmodel: BluetoothViewModel) {
    var isServer by remember { mutableStateOf(false) }
    var receivedMessage by remember { mutableStateOf("") }
    var sendMessage by remember { mutableStateOf("") }
    var connectionSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val devices by bluetoothViewmodel.discoveredDevices.collectAsState()
    val isScanning by bluetoothViewmodel.isDiscovering.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bluetooth Connection", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isServer = !isServer
            if (isServer) {
                coroutineScope.launch {
                    connectionSocket = bluetoothViewmodel.startServer()
                    if (connectionSocket == null) {
                        isServer = false
                        Toast.makeText(context, "Failed to start server", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                coroutineScope.launch {
                    bluetoothViewmodel.startDiscovery()
                }
            }
        }) {
            Text(if (isServer) "Stop Server" else "Start Server")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = sendMessage,
            onValueChange = { sendMessage = it },
            label = { Text("Enter Message") }
)

        Button(onClick = {
            connectionSocket?.let { bluetoothViewmodel.sendData(it, sendMessage) }
        }) {
            Text("Send Message")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            connectionSocket?.let {
                receivedMessage = bluetoothViewmodel.receiveData(it) ?: "No message received"
            }
        }) {
            Text("Receive Message")
        }

        Text("Received: $receivedMessage", fontSize = 16.sp)

        HorizontalDivider()

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(          // Bluetooth permissions granted
                        context, Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED){

                    if (!isScanning)
                        bluetoothViewmodel.startDiscovery()
                    else
                        bluetoothViewmodel.stopDiscovery()
                }
                else{                                           // Missing Bluetooth permissions
                    Toast.makeText(context, "Missing Bluetooth permissions", Toast.LENGTH_SHORT).show()
                }
            }
        )
        {
            Text(
                if (isScanning) "Stop Scanning" else "Start Scanning"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(devices.size) { index ->
                val device = devices[index]
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED){
                    Log.e("StartDiscovery", "Missing Bluetooth permissions")
                } else {
                    Text(device.name ?: "Unknown Device", fontSize = 16.sp)
                }

            }
        }
    }
}

