package com.fiax.hdr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.fiax.hdr.data.bluetooth.ActivityProvider
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.di.ActivityProviderEntryPoint
import com.fiax.hdr.di.BluetoothManagerEntryPoint
import com.fiax.hdr.ui.components.scaffold.MainScaffold
import com.fiax.hdr.ui.theme.HDRTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var activityProvider: ActivityProvider

    private lateinit var bluetoothCustomManager: BluetoothCustomManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityProvider = EntryPointAccessors.fromActivity(
            this,
            ActivityProviderEntryPoint::class.java
        ).activityProvider()

        activityProvider.setActivity(this)

        bluetoothCustomManager = EntryPointAccessors.fromActivity(
            this,
            BluetoothManagerEntryPoint::class.java
        ).bluetoothCustomManager()

        // Define an ActivityResultLauncher for enabling Bluetooth
        val enableBluetoothLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result from Bluetooth enable request
                bluetoothCustomManager.handleEnableActivityResult(result.resultCode)
            }

        bluetoothCustomManager.setEnableBluetoothLauncher(enableBluetoothLauncher)

        val discoverableBluetoothLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result from Bluetooth enable request
                bluetoothCustomManager.handleDiscoverableActivityResult(result.resultCode)
            }

        bluetoothCustomManager.setDiscoverableBluetoothLauncher(discoverableBluetoothLauncher)

        enableEdgeToEdge()

        setContent {
            HDRTheme {
                val navController = rememberNavController()
                MainScaffold(navController, bluetoothCustomManager)
            }
        }

        // Register receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        }
        registerReceiver(bluetoothCustomManager.bluetoothReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothCustomManager.bluetoothReceiver)
        bluetoothCustomManager.deinitialize()
        activityProvider.setActivity(null)
    }
}


