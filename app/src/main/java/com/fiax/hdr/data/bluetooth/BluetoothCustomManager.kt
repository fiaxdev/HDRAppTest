package com.fiax.hdr.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
import com.fiax.hdr.data.mapper.PatientSerializer
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.utils.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothCustomManager @Inject constructor(){

    private val appContext = HDRApp.getAppContext()
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        appContext.getSystemService(BluetoothManager::class.java)?.adapter
    }

    private val _receivedPatients = MutableSharedFlow<Patient>()
    val receivedPatients: SharedFlow<Patient> = _receivedPatients

    private val appName = appContext.getString(R.string.app_name)
    private val appUuid = UUID.fromString(appContext.getString(R.string.app_uuid))
    private val disconnectCode = appContext.getString(R.string.disconnect_request_code)

    private val _isServerOn = MutableStateFlow(false)
    val isServerOn: StateFlow<Boolean> = _isServerOn

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering

    // ------------Devices------------------
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private var _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices

    // ------------Sockets------------------
    private val _connectionSocket = MutableStateFlow<BluetoothSocket?>(null)
    val connectionSocket: StateFlow<BluetoothSocket?> = _connectionSocket

    private var serverSocket: BluetoothServerSocket? = null

    // ----------------------Connection status----------------------
    private var _connectionStatus = MutableStateFlow("")
    val connectionStatus: StateFlow<String> = _connectionStatus

    // -------------Enabling result-----------------
    private var _enablingResult = MutableStateFlow("")
    val enablingResult: StateFlow<String> = _enablingResult

    val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    _isDiscovering.value = true
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isDiscovering.value = false
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    else
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        _discoveredDevices.value += it
                    }
                }
            }
        }
    }

    // Mutable reference to launcher set by the Activity
    private var enableBluetoothLauncher: ActivityResultLauncher<Intent>? = null

    fun initialize(coroutineScope: CoroutineScope){
        ensureBluetoothEnabled(
            onEnabled = {
                fetchPairedDevices()
                coroutineScope.launch { startServer() }
            },
            onDenied = {
                updateEnablingResult(appContext.getString(R.string.bluetooth_server_not_started))
            },
        )
    }

    // Setup
    fun setEnableBluetoothLauncher(launcher: ActivityResultLauncher<Intent>) {
        enableBluetoothLauncher = launcher
    }

    private fun updateServerStatus(isServerOn: Boolean) {
        _isServerOn.value = isServerOn
    }

    private fun updateEnablingResult(result: String){
        _enablingResult.value = result
    }

    private fun updateConnectionStatus(connectionStatus: String) {
        _connectionStatus.value = connectionStatus
    }

    private fun setConnectionSocket(socket: BluetoothSocket?){
        _connectionSocket.value = socket
    }

    @SuppressLint("MissingPermission")
    fun fetchPairedDevices(){
        ensureBluetoothEnabled(
            onEnabled = {
                val pairedDevices = bluetoothAdapter?.bondedDevices
                if (pairedDevices != null)
                    _pairedDevices.value = pairedDevices.toList()
            }
        )
    }

    // Function to ensure Bluetooth is enabled, using the ActivityResultLauncher
    fun ensureBluetoothEnabled(
        onEnabled: () -> Unit,
        onDenied: () -> Unit = {updateEnablingResult(appContext.getString(R.string.bluetooth_denied))},
        onNotSupported: () -> Unit = {updateEnablingResult(appContext.getString(R.string.bluetooth_not_supported))},
        onMissingPermission: () -> Unit = {updateEnablingResult(appContext.getString(R.string.bluetooth_missing_permissions))},
    ) {
        if (!isBluetoothSupported()) {
            onNotSupported()
        } else {

            if (PermissionHelper.hasPermissions(context = appContext)){
                launchIntentIfNotEnabled(
                    onEnabled = onEnabled,
                    onDenied = onDenied
                )
            } else
                onMissingPermission()
        }
    }

    private fun launchIntentIfNotEnabled(
        onEnabled: () -> Unit,
        onDenied: () -> Unit = {updateEnablingResult(appContext.getString(R.string.bluetooth_denied))},
    ) {
        if (!isBluetoothEnabled()) {
            launchBluetoothIntent()
            // Save the callback to be used when the result is received
            bluetoothEnableCallback = object : BluetoothEnableCallback {
                override fun onBluetoothEnabled() {
                    onEnabled()
                }

                override fun onBluetoothDenied() {
                    onDenied()
                }
            }
        } else
            onEnabled()
    }

    private fun launchBluetoothIntent(){
        // Bluetooth is not enabled, request to enable it
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        // Launch the intent to enable Bluetooth
        enableBluetoothLauncher!!.launch(enableBtIntent)
    }

    // Handle the Bluetooth enable result in the Activity
    fun handleActivityResult(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            bluetoothEnableCallback?.onBluetoothEnabled()
        } else {
            bluetoothEnableCallback?.onBluetoothDenied()
        }
    }

    interface BluetoothEnableCallback {
        fun onBluetoothEnabled()
        fun onBluetoothDenied()
    }

    private var bluetoothEnableCallback: BluetoothEnableCallback? = null

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    private fun attemptStartServer(coroutineScope: CoroutineScope) {
        ensureBluetoothEnabled(
            onEnabled = {
                coroutineScope.launch{
                    startServer()
                }
            },
            onDenied = {
                updateEnablingResult(appContext.getString(R.string.bluetooth_server_not_started))
            },
            onNotSupported = {
            },
            onMissingPermission = {
            }
        )
    }


    private suspend fun startServer() {

        var enabled = false

        ensureBluetoothEnabled(
            onEnabled = {
                enabled = true
            },
            onDenied = {
                updateEnablingResult(appContext.getString(R.string.bluetooth_server_not_started))
            },
        )

        if (enabled){
            try {
                if (isBluetoothEnabled()) {
                    val server = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                        appName,
                        appUuid
                    )
                    serverSocket = server

                    updateServerStatus(true)
                    // Accept client connection in the background (Blocking)
                    withContext(Dispatchers.IO) {
                        val socket = acceptClientConnection()
                        setConnectionSocket(socket)
                        if (socket != null)
                            listenForData()
                    }
                }
            } catch (e: SecurityException) {
                Log.e("BluetoothServer", "Permission denied: ${e.message}")
                stopServer()
            } catch (e: IOException) {
                Log.e("BluetoothServer", "Could not open server socket: ${e.message}")
                stopServer()
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun acceptClientConnection(): BluetoothSocket? {
        return withContext(Dispatchers.IO) { // Run in background thread
            try {
                val connectedSocket = serverSocket?.accept()
                Log.d("BluetoothServer", "Client connected: ${connectedSocket?.remoteDevice?.name}")
                connectedSocket
            } catch (e: IOException) {
                Log.e("BluetoothServer", "Error accepting connection: ${e.message}")
                null
            } finally {
                serverSocket?.close()
            }
        }
    }

    fun stopServer() {
        try {
            // Close server socket
            closeServerSocket()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun connectToServer(device: BluetoothDevice): BluetoothSocket? {
        if (!PermissionHelper.hasPermissions(context = appContext)) {
            Log.e("BluetoothClient", "Missing Bluetooth permissions")
            return null
        }

        return withContext(Dispatchers.IO) { // Run in background thread
            try {
                val socket = device.createRfcommSocketToServiceRecord(appUuid)
                // Cancel discovery before connecting
                bluetoothAdapter?.cancelDiscovery()
                Log.d("BluetoothClient", "Connecting to server...")
                socket.connect()
                Log.d("BluetoothClient", "Connected to server!")
                setConnectionSocket(socket)
                socket // Return the connected socket
            } catch (e: SecurityException) {
                Log.e("BluetoothClient", "Permission denied: ${e.message}")
                null
            } catch (e: IOException) {
                Log.e("BluetoothClient", "Could not connect: ${e.message}")
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        ensureBluetoothEnabled(
            onEnabled = {
                if (bluetoothAdapter?.isDiscovering == true) {
                    bluetoothAdapter?.cancelDiscovery()
                    Handler(Looper.getMainLooper()).postDelayed({
                        bluetoothAdapter?.startDiscovery()
                    }, 500)  // Delay of 500ms (half a second) to allow cancelDiscovery() to complete
                } else
                    bluetoothAdapter?.startDiscovery()
            }
        )
    }

    // Stop discovery
    fun stopDiscovery() {
        if (ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED){
            Log.e("StopDiscovery", "Missing Bluetooth permissions")
        } else {
            bluetoothAdapter?.cancelDiscovery()
        }
    }

    fun sendData( message: String) {
        if (connectionSocket.value == null) {
            Log.e("Bluetooth", "Cannot send data: Socket is null")
            return
        }
        try {
            val outputStream = connectionSocket.value!!.outputStream
            outputStream.write(message.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            Log.d("Bluetooth", "Message sent: $message")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error sending data: ${e.message}")
            throw e // Rethrow the exception to let the caller know there was an error
        }
    }

    private fun closeConnectionSocket() {
        try {
            connectionSocket.value?.close()
            setConnectionSocket(null)
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error closing socket: ${e.message}")
            throw e // Rethrow to let the caller know about the error
        }
    }

    private fun closeServerSocket() {
        try {
            serverSocket?.close()
            serverSocket = null
            updateServerStatus(false)
            setConnectionSocket(null)
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error closing server socket: ${e.message}")
            throw e // Rethrow to let the caller know about the error
        }
    }

    private suspend fun listenForData() {
        try {
            val inputStream = connectionSocket.value!!.inputStream
            val buffer = ByteArray(2048)

            while (true) { // Keep listening for messages
                val bytesRead = withContext(Dispatchers.IO) {
                    inputStream.read(buffer)
                } // Blocks until data is received
                val message = String(buffer, 0, bytesRead)

                val envelope = Json.decodeFromString(BluetoothEnvelope.serializer(), message)

                when (envelope.type) {
                    "data" -> {
                        val patient = PatientSerializer.deserialize(envelope.payload)
                        _receivedPatients.emit(patient)
                    }
                    "code" -> {
                        when (val command = envelope.payload.toString(Charsets.UTF_8)) {
                            disconnectCode -> restartServer()
                            else -> Log.w("Bluetooth", "Unknown command: $command")
                        }
                    }
                    else -> Log.w("Bluetooth", "Unknown type: ${envelope.type}")
                }
            }
        } catch (e: IOException) {
            Log.e("Bluetooth", "Connection lost: ${e.message}")
            updateConnectionStatus(appContext.getString(R.string.bluetooth_connection_lost)) // Notify ViewModel when connection is lost
        }
    }

//    private suspend fun listenForPatients(inputStream: InputStream) {
//        val buffer = ByteArray(2048) // adjust size as needed
//
//        while (true) {
//            val bytesRead =
//                withContext(Dispatchers.IO) {
//                    inputStream.read(buffer)
//                }
//            if (bytesRead > 0) {
//                val data = buffer.copyOfRange(0, bytesRead)
//
//                try {
//                    val patient = PatientSerializer.deserialize(data)
//                    _receivedPatients.emit(patient) // emit the Patient
//                } catch (e: Exception) {
//                    Log.e("BluetoothCustomManager", "Error deserializing patient", e)
//                }
//            }
//        }
//    }

    private fun restartServer(){
        stopServer()
        attemptStartServer(CoroutineScope(Dispatchers.IO))
    }

    fun disconnect(){
        try {// Send a disconnect message
            sendDisconnectionRequest()
            // Close the socket
            closeConnectionSocket()
        } catch (e: Exception){
            Log.e("Bluetooth", "Error disconnecting: ${e.message}")
        }
    }

    private fun sendDisconnectionRequest(){
        val commandBytes = disconnectCode.toByteArray(Charsets.UTF_8)
        val envelope = BluetoothEnvelope(
            type = "code",
            payload = commandBytes
        )
        val jsonEnvelope = Json.encodeToString(BluetoothEnvelope.serializer(), envelope)
        sendData(jsonEnvelope)
    }

    fun makeDeviceDiscoverable(activity: Activity, duration: Int = 60) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
        }
        activity.startActivity(discoverableIntent)
    }

    private fun isBluetoothSupported(): Boolean{
        return bluetoothAdapter != null
    }
}




