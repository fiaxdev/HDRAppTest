package com.fiax.hdr.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fiax.hdr.ui.components.BluetoothPermissionHandler
import com.fiax.hdr.ui.components.NoPermissionMessage
import com.fiax.hdr.ui.components.bluetooth.devices.DeviceList
import com.fiax.hdr.ui.components.util.TitleText
import com.fiax.hdr.viewmodel.BluetoothViewModel
import kotlinx.coroutines.launch

@Composable
fun BluetoothScreen(navController: NavHostController){

    val bluetoothViewModel: BluetoothViewModel = hiltViewModel()

    var sendMessage by remember { mutableStateOf("") }
    val connectionSocket by bluetoothViewModel.connectionSocket.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isServer by bluetoothViewModel.isServerOn.collectAsState()
    val isScanning by bluetoothViewModel.isDiscovering.collectAsState()
    val toastMessage by bluetoothViewModel.toastMessage.collectAsState()
    val receivedMessage by bluetoothViewModel.receivedMessages.collectAsState()
    val hasPermission by bluetoothViewModel.hasPermissions.collectAsState()
    val connectionStatus by bluetoothViewModel.connectionStatus.collectAsState()

    LaunchedEffect(Unit) {
        bluetoothViewModel.getBondedDevices()
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            toastMessage.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                bluetoothViewModel.onToastShown() // Reset after showing
            }
        }
    }

    // Handles permission requests before showing the UI
    BluetoothPermissionHandler(
        context = context,
        onPermissionGranted = {
            bluetoothViewModel.updatePermissions(context)
        }
    )

    if (hasPermission) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleText("Bluetooth Screen")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("HomeScreen", "Button clicked. isServer: $isServer")
                        coroutineScope.launch {
                            if (isServer) {
                                bluetoothViewModel.stopServer()
                            } else {
                                bluetoothViewModel.startServer(context as Activity)
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
                    connectionSocket?.let { bluetoothViewModel.sendMessage(it, sendMessage) }
                }) {
                    Text("Send Message")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Received: $receivedMessage", fontSize = 16.sp)

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isScanning)
                            bluetoothViewModel.startDiscovery(context as Activity)
                        else
                            bluetoothViewModel.stopDiscovery()
                    }
                ) {
                    Text(
                        if (isScanning) "Stop Scanning" else "Start Scanning"
                    )
                }

                Text(text = connectionStatus, style = MaterialTheme.typography.bodyLarge)

                DeviceList(bluetoothViewModel)
            }
        }
    } else
        NoPermissionMessage(context, bluetoothViewModel, onBack = { navController.popBackStack() })
}