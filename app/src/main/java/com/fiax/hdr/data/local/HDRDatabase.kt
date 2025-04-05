package com.fiax.hdr.data.local

import com.fiax.hdr.data.model.Patient

@androidx.room.Database(entities = [Patient::class], version = 1)
abstract class HDRDatabase : androidx.room.RoomDatabase() {
    abstract fun patientDao(): PatientDao
    // ... other DAOs
}