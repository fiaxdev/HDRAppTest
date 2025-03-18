package com.fiax.hdr.ui.viewmodel

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class BluetoothViewModel(private val bluetoothCustomManager: BluetoothCustomManager, private val enableBluetoothLauncher: ActivityResultLauncher<Intent>): ViewModel()  {

    private val appContext = HDRApp.getAppContext()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering

    private val _isServerOn = MutableStateFlow(false)
    val isServerOn: StateFlow<Boolean> = _isServerOn.asStateFlow()

    // To use if type consistency is needed (instead of .asStateFlow())
    //
    // that is to always have an explicit type (StateFlow<T>)
    //
//    private val _connectionStatus = MutableStateFlow("Not connected")
//    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _connectionStatus = MutableStateFlow("Not connected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    //---------------------------------------Variables managing----------------------------------------------------

    // Update state when discovery stops (register receiver in Activity)
    fun updateDiscoveryState(isDiscovering: Boolean) {
        _isDiscovering.value = isDiscovering
    }

    // Reset toast message after showing
    fun onToastShown() {
        _toastMessage.value = ""
    }

    fun updateToastMessage(message: String) {
        _toastMessage.value = message
    }

    // Add discovered device to list if not already in
    fun addDiscoveredDevice(device: BluetoothDevice) {
        if (_discoveredDevices.value.contains(device)) return
        _discoveredDevices.value += device
    }

    // Set server status
    fun updateServerStatus(isServerOn: Boolean) {
        Log.d("BluetoothViewModel", "Updating server status to: $isServerOn")
        _isServerOn.value = isServerOn
        Log.d("BluetoothViewModel", "Updated isServerOn: ${_isServerOn.value}")
    }

    //---------------------------------------Bluetooth functionalities---------------------------------------------

    fun startDiscovery() {
        ensureBluetoothEnabled(
            onEnabled = {
                bluetoothCustomManager.startDiscovery()
                _isDiscovering.value = true
            },
            onDenied = { updateToastMessage(appContext.getString(R.string.bluetooth_denied)) },
            onNotSupported = { updateToastMessage(appContext.getString(R.string.bluetooth_not_supported)) }
        )
    }

    fun stopDiscovery() {
        ensureBluetoothEnabled(
            onEnabled = {
                bluetoothCustomManager.stopDiscovery()
                _isDiscovering.value = false
            },
            onDenied = { updateToastMessage(appContext.getString(R.string.bluetooth_denied)) },
            onNotSupported = { updateToastMessage(appContext.getString(R.string.bluetooth_not_supported)) }
        )
    }



    fun connectToDevice(device: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED){
            return
        }
        ensureBluetoothEnabled(
            onEnabled = {
                _connectionStatus.value = "Connecting to ${device.name}..."
                var socket: BluetoothSocket? = null
                viewModelScope.launch(Dispatchers.IO) {
                    socket = bluetoothCustomManager.connectToServer(device)
                    if (socket != null) {
                        _connectionStatus.value = "Connected to ${device.name}"
                    } else {
                        _connectionStatus.value = "Failed to connect to ${device.name}"
                    }
                }
            },
            onDenied = { updateToastMessage(appContext.getString(R.string.bluetooth_denied)) },
            onNotSupported = { updateToastMessage(appContext.getString(R.string.bluetooth_not_supported)) }
        )

    }

    suspend fun startServer(): BluetoothSocket? {
        Log.d("BluetoothViewModel", "startServer() called")
        return suspendCoroutine { continuation ->
            ensureBluetoothEnabled(
                onEnabled = {
                    viewModelScope.launch(Dispatchers.IO) {
                        Log.d("BluetoothViewModel", "Starting Bluetooth server")
                        val socket = bluetoothCustomManager.startBluetoothServer()
                        Log.d("BluetoothViewModel", "Bluetooth server started")
                        withContext(Dispatchers.Main) {
                            if (socket != null) {
                                Log.d("BluetoothViewModel", "Bluetooth server started successfully")
                                updateServerStatus(true)
                                updateToastMessage(appContext.getString(R.string.bluetooth_server_started))
                            } else {
                                Log.d("BluetoothViewModel", "Bluetooth server failed to start")
                                updateServerStatus(false)
                                updateToastMessage(appContext.getString(R.string.bluetooth_server_not_started))
                            }
                            continuation.resume(socket)
                        }
                    }
                },
                onDenied = {
                    updateToastMessage(appContext.getString(R.string.bluetooth_denied))
                    continuation.resume(null) // Resume with null in case of denial
                },
                onNotSupported = {
                    updateToastMessage(appContext.getString(R.string.bluetooth_not_supported))
                    continuation.resume(null) // Resume with null in case of unsupported device
                }
            )
        }
    }

    fun stopServer() {
        bluetoothCustomManager.stopBluetoothServer()
        updateServerStatus(false)
        updateToastMessage(appContext.getString(R.string.bluetooth_server_stopped))
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothCustomManager.isBluetoothEnabled()
    }

    fun sendData(socket: BluetoothSocket, data: String) {
        ensureBluetoothEnabled(
            onEnabled = {
                bluetoothCustomManager.sendData(socket, data)
            },
            onDenied = { updateToastMessage(appContext.getString(R.string.bluetooth_denied)) },
            onNotSupported = { updateToastMessage(appContext.getString(R.string.bluetooth_not_supported)) }
        )
    }

    fun receiveData(socket: BluetoothSocket): String? {
        var data: String? = null
        ensureBluetoothEnabled(
            onEnabled = {
                data = bluetoothCustomManager.receiveData(socket)
            },
            onDenied = { updateToastMessage(appContext.getString(R.string.bluetooth_denied)) },
            onNotSupported = { updateToastMessage(appContext.getString(R.string.bluetooth_not_supported)) }
        )
        return data
    }

    fun hasBluetoothPermissions(): Boolean {
        return bluetoothCustomManager.hasBluetoothPermissions() ?: false
    }

    fun ensureBluetoothEnabled(onEnabled: () -> Unit, onDenied: () -> Unit, onNotSupported: () -> Unit) {
        bluetoothCustomManager.ensureBluetoothEnabled(enableBluetoothLauncher, onEnabled, onDenied, onNotSupported)
    }
}
