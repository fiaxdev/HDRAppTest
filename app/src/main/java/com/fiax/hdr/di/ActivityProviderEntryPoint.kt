package com.fiax.hdr.di

import com.fiax.hdr.data.bluetooth.ActivityProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ActivityProviderEntryPoint {
    fun activityProvider(): ActivityProvider
}