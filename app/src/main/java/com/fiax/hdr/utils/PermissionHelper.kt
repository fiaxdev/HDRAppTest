package com.fiax.hdr.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Helper class to handle Bluetooth permissions based on Android version.
 */
object PermissionHelper {

    private const val REQUEST_CODE = 1001

    /**
     * Returns the required Bluetooth permissions based on API level.
     * Uses BLUETOOTH and BLUETOOTH_ADMIN for API < 31.
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }

    /**
     * Checks if all required Bluetooth permissions are granted.
     */
    fun hasPermissions(context: Context): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Requests Bluetooth permissions if needed.
     */
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, getRequiredPermissions(), REQUEST_CODE)
    }
}