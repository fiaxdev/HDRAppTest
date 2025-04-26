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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
import com.fiax.hdr.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class BluetoothCustomManager {

    private val appContext = HDRApp.getAppContext()
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        appContext.getSystemService(BluetoothManager::class.java)?.adapter
    }

    private var serverSocket: BluetoothServerSocket? = null
//    private var connectedSocket: BluetoothSocket? = null

    private val appName = appContext.getString(R.string.app_name)
    private val appUuid = UUID.fromString(appContext.getString(R.string.app_uuid))

    // Mutable reference to launcher set by the Activity
    private var enableBluetoothLauncher: ActivityResultLauncher<Intent>? = null

    // Setup
    fun setEnableBluetoothLauncher(launcher: ActivityResultLauncher<Intent>) {
        enableBluetoothLauncher = launcher
    }

    // Function to ensure Bluetooth is enabled, using the ActivityResultLauncher
    fun ensureBluetoothEnabled(
        onEnabled: () -> Unit,
        onDenied: () -> Unit,
        onNotSupported: () -> Unit,
        onMissingPermission: () -> Unit,
    ) {
        if (!isBluetoothSupported()) {
            onNotSupported()
        } else {

            if (PermissionHelper.hasPermissions(context = appContext)){
                if (!isBluetoothEnabled()) {
                    // Bluetooth is not enabled, request to enable it
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    // Launch the intent to enable Bluetooth
                    enableBluetoothLauncher!!.launch(enableBtIntent)
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
            } else
                onMissingPermission()
        }
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

    fun startBluetoothServer(): BluetoothServerSocket? {
        return try {
            val server = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                appName,
                appUuid
            )
            serverSocket = server
            Log.d("BluetoothServer", "Server started. Waiting for connection...")
            server // Return the server socket immediately
        } catch (e: SecurityException) {
            Log.e("BluetoothServer", "Permission denied: ${e.message}")
            null
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Could not open server socket: ${e.message}")
            null
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

    fun stopBluetoothServer() {
        try {
            //disconnect()
            serverSocket?.close() // Close server socket
            serverSocket = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//    fun disconnect() {
//        try {
//            // Send a disconnect message to the other device
//            sendData(connectedSocket!!, appContext.getString(R.string.disconnect_request))
//            // Close the socket
//            connectedSocket?.close()
//            connectedSocket = null
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
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
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
            Handler(Looper.getMainLooper()).postDelayed({
                bluetoothAdapter?.startDiscovery()
            }, 500)  // Delay of 500ms (half a second) to allow cancelDiscovery() to complete
        } else
            bluetoothAdapter?.startDiscovery()
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

    fun sendData(socket: BluetoothSocket?, message: String) {
        if (socket == null) {
            Log.e("Bluetooth", "Cannot send data: Socket is null")
            return
        }
        try {
            val outputStream = socket.outputStream
            outputStream.write(message.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            Log.d("Bluetooth", "Message sent: $message")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error sending data: ${e.message}")
            throw e // Rethrow the exception to let the caller know there was an error
        }
    }

    fun closeSocket(socket: BluetoothSocket?) {
        try {
            socket?.close()
            Log.d("Bluetooth", "Socket closed successfully.")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error closing socket: ${e.message}")
            throw e // Rethrow to let the caller know about the error
        }
    }

//    fun listenForData(socket: BluetoothSocket, onMessageReceived: (String) -> Unit) {
//        thread {
//            try {
//                val inputStream = socket.inputStream
//                val buffer = ByteArray(1024)
//                while (true) {
//                    val bytesRead = inputStream.read(buffer)
//                    val receivedMessage = String(buffer, 0, bytesRead, Charsets.UTF_8)
//                    onMessageReceived(receivedMessage)  // Callback to ViewModel
//                }
//            } catch (e: IOException) {
//                Log.e("Bluetooth", "Error receiving data: ${e.message}")
//            }
//        }
//    }

    suspend fun listenForData(socket: BluetoothSocket, onMessageReceived: (String) -> Unit, onConnectionLost: (String) -> Unit) {
        try {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)

            while (true) { // Keep listening for messages
                val bytesRead = withContext(Dispatchers.IO) {
                    inputStream.read(buffer)
                } // Blocks until data is received
                val message = String(buffer, 0, bytesRead)

                if (message == appContext.getString(R.string.disconnect_request_code)){
                    onConnectionLost(appContext.getString(R.string.bluetooth_connection_lost_due_to_remote_device_disconnection)) // Notify ViewModel when connection is lost
                    break
                }

                // Call a callback to notify ViewModel
                else{
                    onMessageReceived(message)
                }
            }
        } catch (e: IOException) {
            Log.e("Bluetooth", "Connection lost: ${e.message}")
            onConnectionLost(appContext.getString(R.string.bluetooth_connection_lost)) // Notify ViewModel when connection is lost
        }
    }

    @SuppressLint("MissingPermission")
    fun getBondedDevices(): Set<BluetoothDevice>? {
        return bluetoothAdapter?.bondedDevices
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




