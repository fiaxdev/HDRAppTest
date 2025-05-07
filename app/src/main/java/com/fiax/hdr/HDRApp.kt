package com.fiax.hdr

import android.app.Application
import android.content.Context
import com.fiax.hdr.utils.PermissionHelper
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class HDRApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        PermissionHelper.init(this)
    }

    companion object {
        private lateinit var instance: HDRApp
        fun getAppContext(): Context = instance.applicationContext
    }
}