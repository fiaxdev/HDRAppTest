package com.fiax.hdr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiax.hdr.data.model.PatientEntity

@Database(entities = [PatientEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class HDRDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    // ... other DAOs
}
