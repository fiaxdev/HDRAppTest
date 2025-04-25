package com.fiax.hdr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.fiax.hdr.ui.components.scaffold.MainScaffold
import com.fiax.hdr.ui.theme.HDRTheme
import com.fiax.hdr.viewmodel.BluetoothViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //Bluetooth

    private val bluetoothViewModel: BluetoothViewModel by viewModels()

    // Define an ActivityResultLauncher for enabling Bluetooth
    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result from Bluetooth enable request
            bluetoothViewModel.handleActivityResult(result.resultCode)
        }

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
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        else
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        bluetoothViewModel.addDiscoveredDevice(it)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothViewModel.setEnableBluetoothLauncher(enableBluetoothLauncher)

        enableEdgeToEdge()

        setContent {
            HDRTheme {
                val navController = rememberNavController()
                MainScaffold(navController, bluetoothViewModel)
            }
        }

        // Register receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(bluetoothReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }
}


