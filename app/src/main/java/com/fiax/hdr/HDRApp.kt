package com.fiax.hdr

import android.app.Application
import android.content.Context
import com.fiax.hdr.di.ServiceLocator

class HDRApp : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: HDRApp
        fun getAppContext(): Context = instance.applicationContext
    }
}