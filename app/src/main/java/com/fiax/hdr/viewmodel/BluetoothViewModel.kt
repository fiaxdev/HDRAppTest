package com.fiax.hdr.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class BluetoothViewModel(
    private val bluetoothCustomManager: BluetoothCustomManager,
    private val enableBluetoothLauncher: ActivityResultLauncher<Intent>,
): ViewModel()  {

    private val appContext = HDRApp.getAppContext()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices

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

    private val _receivedMessages = MutableStateFlow("")
    val receivedMessages: StateFlow<String> = _receivedMessages

    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions: StateFlow<Boolean> = _hasPermissions.asStateFlow()

    private val _connectionSocket = MutableStateFlow<BluetoothSocket?>(null)
    val connectionSocket: StateFlow<BluetoothSocket?> = _connectionSocket

//---------------------------------------Variables managing----------------------------------------------------

    // Update state when discovery stops (register receiver in Activity)
    fun updateDiscoveryState(isDiscovering: Boolean) {
        _isDiscovering.value = isDiscovering
    }

    // Reset toast message after showing
    fun onToastShown() {
        _toastMessage.value = ""
    }

    private fun updateToastMessage(message: String) {
        _toastMessage.value = message
    }

    // Add discovered device to list if not already in
    fun addDiscoveredDevice(device: BluetoothDevice) {
        if (_discoveredDevices.value.contains(device) or
            _pairedDevices.value.contains(device)
        )
            return
        _discoveredDevices.value += device
    }

    // Set server status
    private fun updateServerStatus(isServerOn: Boolean) {
        _isServerOn.value = isServerOn
    }

    // Set connection status
    private fun updateConnectionStatus(connectionStatus: String) {
        _connectionStatus.value = connectionStatus
    }

    // Set connection socket
    private fun updateConnectionSocket(connectionSocket: BluetoothSocket?) {
        _connectionSocket.value = connectionSocket
    }

//---------------------------------------Permissions functionalities-------------------------------------------
    fun updatePermissions(context: Context) {
        _hasPermissions.value = PermissionHelper.hasPermissions(context)
    }
//---------------------------------------Bluetooth functionalities---------------------------------------------

  //-------------------------------------Paired devices------------------------------------------------------
    fun getBondedDevices() {
        ensureBluetoothEnabled(
            onEnabled = {
                val pairedDevices = bluetoothCustomManager.getBondedDevices()
                if (pairedDevices != null)
                  _pairedDevices.value = pairedDevices.toList()
            }
        )
    }
  //-------------------------------------Discovery------------------------------------------------------------

    fun startDiscovery(activity: Activity) {
        ensureBluetoothEnabled(
            onEnabled = {
                bluetoothCustomManager.startDiscovery()
                updateDiscoveryState(true)
                makeDeviceDiscoverable(activity = activity)
            }
        )
    }

    fun stopDiscovery() {
        ensureBluetoothEnabled(
            onEnabled = {
                bluetoothCustomManager.stopDiscovery()
                updateDiscoveryState(false)
            }
        )
    }

  //-------------------------------------Connection & Server------------------------------------------------------------

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {

        ensureBluetoothEnabled(
            onEnabled = {
                _connectionStatus.value = "Connecting..."
                var socket: BluetoothSocket?
                viewModelScope.launch(Dispatchers.IO) {
                    socket = bluetoothCustomManager.connectToServer(device)
                    if (socket != null) {
                        updateConnectionStatus("Connected")
                        updateConnectionSocket(socket)
                        startListeningForMessages(socket!!)
                    } else {
                        updateConnectionStatus("Failed to connect to ${device.name}")
                    }
                }
            }
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun startServer(activity: Activity): BluetoothSocket? {
        return suspendCoroutine { continuation ->

            ensureBluetoothEnabled(
                onEnabled = {
                    viewModelScope.launch(Dispatchers.IO) {
                        val serverSocket = bluetoothCustomManager.startBluetoothServer()

                        withContext(Dispatchers.Main) {
                            if (serverSocket != null) {
                                updateServerStatus(true) // Update UI instantly
                                updateToastMessage(appContext.getString(R.string.bluetooth_server_started))
                                makeDeviceDiscoverable(activity = activity)

                                // Now wait for a client connection in the background
                                viewModelScope.launch(Dispatchers.IO) {
                                    val socket = bluetoothCustomManager.acceptClientConnection()
                                    withContext(Dispatchers.Main) {
                                        if (socket != null) {
                                            updateConnectionStatus("Connected")
                                            updateConnectionSocket(socket)
                                            startListeningForMessages(socket)
                                        } else {
                                            updateToastMessage(appContext.getString(R.string.bluetooth_connection_failed))
                                        }
                                        continuation.resume(socket)
                                    }
                                }

                            } else {
                                updateServerStatus(false)
                                updateToastMessage(appContext.getString(R.string.bluetooth_server_not_started))
                                continuation.resume(null)
                            }
                        }
                    }
                },
                onDenied = {
                    updateToastMessage(appContext.getString(R.string.bluetooth_denied))
                    continuation.resume(null)
                },
                onNotSupported = {
                    updateToastMessage(appContext.getString(R.string.bluetooth_not_supported))
                    continuation.resume(null)
                },
                onMissingPermission = {
                    updateToastMessage(appContext.getString(R.string.bluetooth_missing_permissions))
                    continuation.resume(null)
                }
            )
        }
    }

    fun stopServer() {
        bluetoothCustomManager.stopBluetoothServer()
        updateServerStatus(false)
        updateToastMessage(appContext.getString(R.string.bluetooth_server_stopped))
        updateConnectionStatus("Not connected")
        updateConnectionSocket(null)
    }

    fun disconnect() {
        viewModelScope.launch { // launch a coroutine
            try {
                // Send a disconnect message
                bluetoothCustomManager.sendData(connectionSocket.value, appContext.getString(R.string.disconnect_request_code))
                // Close the socket
                bluetoothCustomManager.closeSocket(connectionSocket.value)
            } catch (e: Exception){
                //Handle the error, for example notify the user
            } finally{
                updateConnectionStatus("Not connected")
                updateConnectionSocket(null)
            }
        }
    }

  //----------------------------------Enabling-------------------------------------

    private fun ensureBluetoothEnabled(
        onEnabled: () -> Unit,
        onDenied: () -> Unit = {updateToastMessage(appContext.getString(R.string.bluetooth_denied))},
        onNotSupported: () -> Unit = {updateToastMessage(appContext.getString(R.string.bluetooth_not_supported))},
        onMissingPermission: () -> Unit = {updateToastMessage(appContext.getString(R.string.bluetooth_missing_permissions))}
    ) {
        bluetoothCustomManager.ensureBluetoothEnabled(enableBluetoothLauncher, onEnabled, onDenied, onNotSupported, onMissingPermission)
    }
  //---------------------------------Permissions--------------------------------------------------------

  //-----------------------------------Sending and Receiving------------------------------------------------------------
    fun sendMessage(socket: BluetoothSocket, message: String) {
        viewModelScope.launch {
            bluetoothCustomManager.sendData(socket, message)
        }
    }

    private fun startListeningForMessages(socket: BluetoothSocket) {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothCustomManager.listenForData(
                socket = socket,
                onConnectionLost = {toastMessage ->
                    updateConnectionStatus("Not connected")
                    updateServerStatus(false)
                    updateToastMessage(toastMessage)
                },
                onMessageReceived = { message ->
                    viewModelScope.launch {
                        _receivedMessages.value += message  // Update UI
                    }
                }

            )
        }
    }

    private fun makeDeviceDiscoverable(activity: Activity) {
        ensureBluetoothEnabled(onEnabled = { bluetoothCustomManager.makeDeviceDiscoverable(activity) })
    }
}
