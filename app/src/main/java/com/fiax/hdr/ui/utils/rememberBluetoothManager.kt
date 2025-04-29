package com.fiax.hdr.ui.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.di.BluetoothManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors

// Unused as of now, might be useful in the future

@Composable
fun rememberBluetoothManager(): BluetoothCustomManager {
    val context = LocalContext.current
    return remember {
        EntryPointAccessors.fromActivity(
            context as Activity,
            BluetoothManagerEntryPoint::class.java
        ).bluetoothCustomManager()
    }
}
