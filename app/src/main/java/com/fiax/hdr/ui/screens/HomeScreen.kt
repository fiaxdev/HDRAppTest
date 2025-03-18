package com.fiax.hdr.ui.screens

import android.Manifest
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.fiax.hdr.ui.components.scaffold.DeviceList
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(bluetoothViewModel: BluetoothViewModel) {
    var receivedMessage by remember { mutableStateOf("") }
    var sendMessage by remember { mutableStateOf("") }
    var connectionSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var isServer by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val isScanning by bluetoothViewModel.isDiscovering.collectAsState()
    val toastMessage by bluetoothViewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            toastMessage.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                bluetoothViewModel.onToastShown() // Reset after showing
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bluetooth Connection", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("HomeScreen", "Button clicked. isServer: $isServer")
                coroutineScope.launch {
                    isServer = !isServer
                    if (!isServer) {
                        bluetoothViewModel.stopServer()
                    } else {
                        bluetoothViewModel.startServer()
                    }

                }
            }
        ) {
            Text(if (isServer) "Stop Server" else "Start Server")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = sendMessage,
            onValueChange = { sendMessage = it },
            label = { Text("Enter Message") }
)

        Button(onClick = {
            connectionSocket?.let { bluetoothViewModel.sendData(it, sendMessage) }
        }) {
            Text("Send Message")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            connectionSocket?.let {
                receivedMessage = bluetoothViewModel.receiveData(it) ?: "No message received"
            }
        }) {
            Text("Receive Message")
        }

        Text("Received: $receivedMessage", fontSize = 16.sp)

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(          // Bluetooth permissions granted
                        context, Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED){

                    if (!isScanning)
                        bluetoothViewModel.startDiscovery()
                    else
                        bluetoothViewModel.stopDiscovery()
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

        DeviceList(bluetoothViewModel)
    }
}

