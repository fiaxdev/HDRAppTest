package com.fiax.hdr.ui.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.AndroidViewModel
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothManager = bluetoothAdapter?.let { BluetoothCustomManager() }

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering

    fun startDiscovery() {
        bluetoothManager?.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothManager?.stopDiscovery()
    }

    // Update state when discovery stops (register receiver in Activity)
    fun updateDiscoveryState(isDiscovering: Boolean) {
        _isDiscovering.value = isDiscovering
    }

    suspend fun connectToDevice(device: BluetoothDevice) {
        bluetoothManager?.connectToServer(device)
    }

    suspend fun startServer(): BluetoothSocket? {
        return bluetoothManager?.startBluetoothServer()
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothManager?.isBluetoothEnabled() ?: false
    }

    fun sendData(socket: BluetoothSocket, data: String) {
        bluetoothManager?.sendData(socket, data)
    }

    fun receiveData(socket: BluetoothSocket): String? {
        return bluetoothManager?.receiveData(socket)
    }

    fun hasBluetoothPermissions(): Boolean {
        return bluetoothManager?.hasBluetoothPermissions() ?: false
    }

    fun addDiscoveredDevice(device: BluetoothDevice) {
        if (_discoveredDevices.value.contains(device)) return
        _discoveredDevices.value += device
    }
}
