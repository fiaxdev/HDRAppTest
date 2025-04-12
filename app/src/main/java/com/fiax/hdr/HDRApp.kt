package com.fiax.hdr

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class HDRApp : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: HDRApp
        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        //clearDatabaseAndSyncQueue(context = applicationContext)
    }
}