package com.fiax.hdr.data.bluetooth

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fiax.hdr.HDRApp
import com.fiax.hdr.R
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
    private var connectedSocket: BluetoothSocket? = null

    private val APP_NAME = appContext.getString(R.string.app_name)
    private val APP_UUID = UUID.fromString(appContext.getString(R.string.app_uuid))
    private val SerialConnectionUUID = UUID.fromString(appContext.getString(R.string.serial_connection_uuid))
    private val REQUEST_BLUETOOTH_PERMISSION = 1

    // Function to ensure Bluetooth is enabled, using the ActivityResultLauncher
    fun ensureBluetoothEnabled(
        enableBluetoothLauncher: ActivityResultLauncher<Intent>,
        onEnabled: () -> Unit,
        onDenied: () -> Unit,
        onNotSupported: () -> Unit
    ) {
        if (bluetoothAdapter == null) {
            onNotSupported()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(
                    appContext, Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED){
                    if (!bluetoothAdapter!!.isEnabled) {
                        // Bluetooth is not enabled, request to enable it
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        // Launch the intent to enable Bluetooth
                        enableBluetoothLauncher.launch(enableBtIntent)
                        // Save the callback to be used when the result is received
                        bluetoothEnableCallback = object : BluetoothEnableCallback {
                            override fun onBluetoothEnabled() {
                                onEnabled()
                            }

                            override fun onBluetoothDenied() {
                                onDenied()
                            }
                        }
                    } else {
                        // Bluetooth is already enabled
                        onEnabled()
                    }
                }
            }
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

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }

    fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isBluetoothEnabled(): Boolean {
//        val bluetoothAdapter = (appContext.getSystemService(BluetoothManager::class.java))?.adapter
        return bluetoothAdapter?.isEnabled == true
    }

    fun requestEnableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED)
                if (!isBluetoothEnabled()) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    appContext.startActivity(enableBtIntent)
                }
        }
    }

    suspend fun startBluetoothServer(): BluetoothSocket? {
        if (!hasBluetoothPermissions()) {
            Log.e("BluetoothServer", "Missing Bluetooth permissions")
            return null
        }

        if (bluetoothAdapter == null) {
            Log.e("BluetoothServer", "Bluetooth is not available")
            requestEnableBluetooth()
            return null
        }

        val server: BluetoothServerSocket? = try {
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                APP_NAME,
                APP_UUID
            )
        } catch (e: SecurityException) {
            Log.e("BluetoothServer", "Permission denied: ${e.message}")
            return null
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Could not open server socket: ${e.message}")
            return null
        }

        serverSocket = server
        Log.d("BluetoothServer", "Waiting for connection...")

        return withContext(Dispatchers.IO) { // Run in background thread
            try {
                val socket = serverSocket?.accept()
                connectedSocket = socket
                if (ContextCompat.checkSelfPermission(
                        appContext, Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED){
                    Log.e("BluetoothServer", "Missing Bluetooth permissions")
                }
                Log.d("BluetoothServer", "Device connected: ${socket?.remoteDevice?.name}")
                socket
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
            connectedSocket?.close() // Close connected socket if active
            serverSocket?.close() // Close server socket
            connectedSocket = null
            serverSocket = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun connectToServer(device: BluetoothDevice): BluetoothSocket? {
        if (!hasBluetoothPermissions()) {
            Log.e("BluetoothClient", "Missing Bluetooth permissions")
            return null
        }

        return withContext(Dispatchers.IO) { // Run in background thread
            try {
                val socket = device.createRfcommSocketToServiceRecord(APP_UUID)
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

    fun startDiscovery() {
        if (ContextCompat.checkSelfPermission(
                appContext, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED){
            Log.e("StartDiscovery", "Missing Bluetooth permissions")
        } else {
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
                Handler(Looper.getMainLooper()).postDelayed({
                    bluetoothAdapter?.startDiscovery()
                }, 500)  // Delay of 500ms (half a second) to allow cancelDiscovery() to complete
            } else
                bluetoothAdapter?.startDiscovery()
        }
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

    // Not tested
    fun sendData(socket: BluetoothSocket, data: String) {
        try {
            val outputStream = socket.outputStream
            outputStream.write(data.toByteArray())
            outputStream.flush()
            Log.d("BluetoothServer", "Sent: $data")
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Error sending data: ${e.message}")
        }
    }

    // Not tested
    fun receiveData(socket: BluetoothSocket): String? {
        return try {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            Log.d("BluetoothServer", "Received: $receivedMessage")
            receivedMessage
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Error receiving data: ${e.message}")
            null
        }
    }

//    fun getBondedDevices(): Set<BluetoothDevice>? {
//        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//        pairedDevices?.forEach { device ->
//            val deviceName = device.name
//            val deviceHardwareAddress = device.address // MAC address
//        }
//    }

    fun isBluetoothSupported(): Boolean{
        return bluetoothAdapter != null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
            appContext as Activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            REQUEST_BLUETOOTH_PERMISSION
        )
    }
}




