package com.fiax.hdr.data.bluetooth

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProvider @Inject constructor() {

    private var currentActivity: Activity? = null

    fun setActivity(activity: Activity?) {
        currentActivity = activity
    }

    fun useActivity(callback: (Activity) -> Unit) {
        callback(currentActivity ?: throw IllegalStateException("No activity set"))
    }
}