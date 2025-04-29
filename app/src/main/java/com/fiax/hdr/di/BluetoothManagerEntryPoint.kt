package com.fiax.hdr.di

import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface BluetoothManagerEntryPoint {
    fun bluetoothCustomManager(): BluetoothCustomManager
}

