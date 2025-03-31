package com.fiax.hdr.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
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
import kotlin.concurrent.thread

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

    // Function to ensure Bluetooth is enabled, using the ActivityResultLauncher
    fun ensureBluetoothEnabled(
        enableBluetoothLauncher: ActivityResultLauncher<Intent>,
        onEnabled: () -> Unit,
        onDenied: () -> Unit,
        onNotSupported: () -> Unit,
        onMissingPermission: () -> Unit,
    ) {
        if (bluetoothAdapter == null) {
            onNotSupported()
        } else {

            if (PermissionHelper.hasPermissions(context = appContext)){
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
                } else
                    onEnabled()
            } else
                onMissingPermission()
        }
    }

    fun ensurePermissions(
        requestPermissionsLauncher: ActivityResultLauncher<Array<String>>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (PermissionHelper.hasPermissions(appContext)) {
            onGranted()
        } else {
            permissionsRequestCallback = object : PermissionsRequestCallback {
                override fun onPermissionsGranted() {
                    onGranted()
                }
                override fun onPermissionsDenied() {
                    onDenied()
                }
            }
            requestPermissionsLauncher.launch(PermissionHelper.getRequiredPermissions())
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

    interface PermissionsRequestCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

    private var bluetoothEnableCallback: BluetoothEnableCallback? = null
    private var permissionsRequestCallback: PermissionsRequestCallback? = null

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    suspend fun startBluetoothServer(): BluetoothSocket? {
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
        if (!PermissionHelper.hasPermissions(context = appContext)) {
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

    fun sendData(socket: BluetoothSocket, message: String) {
        try {
            val outputStream = socket.outputStream
            outputStream.write(message.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            Log.d("Bluetooth", "Message sent: $message")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error sending data: ${e.message}")
        }
    }

    fun listenForData(socket: BluetoothSocket, onMessageReceived: (String) -> Unit) {
        thread {
            try {
                val inputStream = socket.inputStream
                val buffer = ByteArray(1024)
                while (true) {
                    val bytesRead = inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytesRead, Charsets.UTF_8)
                    onMessageReceived(receivedMessage)  // Callback to ViewModel
                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error receiving data: ${e.message}")
            }
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

    fun handlePermissionsResult(allGranted: Boolean){
        if (allGranted) {
            permissionsRequestCallback?.onPermissionsGranted()
        } else {
            permissionsRequestCallback?.onPermissionsDenied()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun requestBluetoothPermissions() {
//        ActivityCompat.requestPermissions(
//            appContext as Activity,
//            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//            REQUEST_BLUETOOTH_PERMISSION
//        )
//    }
}




