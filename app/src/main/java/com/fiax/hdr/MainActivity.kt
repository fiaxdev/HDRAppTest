package com.fiax.hdr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.di.BluetoothManagerEntryPoint
import com.fiax.hdr.ui.components.scaffold.MainScaffold
import com.fiax.hdr.ui.theme.HDRTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //Bluetooth
//    private val bluetoothViewModel: BluetoothViewModel by viewModels()

    private lateinit var bluetoothCustomManager: BluetoothCustomManager


//    private val bluetoothReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when (intent?.action) {
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    bluetoothViewModel.updateDiscoveryState(true)
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    bluetoothViewModel.updateDiscoveryState(false)
//                }
//                BluetoothDevice.ACTION_FOUND -> {
//                    val device: BluetoothDevice? =
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
//                        else
//                            @Suppress("DEPRECATION")
//                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    device?.let {
//                        bluetoothViewModel.addDiscoveredDevice(it)
//                    }
//                }
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothCustomManager = EntryPointAccessors.fromActivity(
            this,
            BluetoothManagerEntryPoint::class.java
        ).bluetoothCustomManager()

        // Define an ActivityResultLauncher for enabling Bluetooth
        val enableBluetoothLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result from Bluetooth enable request
                bluetoothCustomManager.handleActivityResult(result.resultCode)
            }

        bluetoothCustomManager.setEnableBluetoothLauncher(enableBluetoothLauncher)

        lifecycleScope.launch {
            bluetoothCustomManager.startServer()
        }

        enableEdgeToEdge()

        setContent {
            HDRTheme {
                val navController = rememberNavController()
                MainScaffold(navController)
            }
        }

        // Register receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(bluetoothCustomManager.bluetoothReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothCustomManager.bluetoothReceiver)
    }
}


