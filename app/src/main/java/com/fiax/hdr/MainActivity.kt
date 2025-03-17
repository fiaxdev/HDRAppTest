package com.fiax.hdr

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.fiax.hdr.data.nfc.NfcCustomManager
import com.fiax.hdr.ui.components.scaffold.MainScaffold
import com.fiax.hdr.ui.theme.HDRTheme
import com.fiax.hdr.ui.viewmodel.BluetoothViewModel
import com.fiax.hdr.ui.viewmodel.NfcViewModel

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var nfcCustomManager: NfcCustomManager
    private val nfcViewModel : NfcViewModel by viewModels()

    //Bluetooth
    private lateinit var bluetoothViewModel: BluetoothViewModel

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    bluetoothViewModel.updateDiscoveryState(true)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    bluetoothViewModel.updateDiscoveryState(false)
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        bluetoothViewModel.addDiscoveredDevice(it)
                    }
                }
            }
        }
    }


    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Bluetooth is enabled
            } else {
                // User denied enabling Bluetooth
            }
        }

    private fun requestBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_BLUETOOTH_PERMISSION)
        }
    }

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bluetoothViewModel = BluetoothViewModel(this.application)
        setContent {
            HDRTheme {
                val navController = rememberNavController()
                MainScaffold(navController, nfcViewModel, bluetoothViewModel)
            }
        }
        checkAndRequestPermissions()//Bluetooth

        if (!bluetoothViewModel.isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
        // Register receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(bluetoothReceiver, filter)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcCustomManager = NfcCustomManager(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_LONG).show()
        } else if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC is disabled. Please enable it in settings.", Toast.LENGTH_LONG).show()
        }

        Log.d("MainActivity", "App started")
    }

    override fun onResume() {
        super.onResume()
        nfcCustomManager.enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        nfcCustomManager.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }

    private fun checkAndRequestPermissions() {

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        if (!permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            Log.d("MainActivity", "Requesting Bluetooth permissions")
            requestPermissionsLauncher.launch(permissions)
        } else {
            Log.d("MainActivity", "All Bluetooth permissions already granted")
        }

    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                Log.d("MainActivity", "All permissions granted")
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Log.w("MainActivity", "Permissions denied. The app may not function correctly.")
                Toast.makeText(this, "Permissions denied. The app may not function correctly.", Toast.LENGTH_LONG).show()
            }
        }
}


