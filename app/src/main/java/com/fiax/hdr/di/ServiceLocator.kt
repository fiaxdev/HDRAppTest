package com.fiax.hdr.di

import android.content.Context
import androidx.room.Room
import com.fiax.hdr.data.local.HDRDatabase
import com.fiax.hdr.data.repository.LocalPatientRepositoryImpl
import com.fiax.hdr.data.repository.PatientRepository

object ServiceLocator {

    private var database: HDRDatabase? = null
    private var patientRepository: PatientRepository? = null

    fun providePatientRepository(context: Context): PatientRepository {
        synchronized(this) {
            return patientRepository ?: createPatientRepository(context)
        }
    }

    private fun createPatientRepository(context: Context): PatientRepository {
        val newRepo = LocalPatientRepositoryImpl(provideAppDatabase(context).patientDao())
        patientRepository = newRepo
        return newRepo
    }

    private fun provideAppDatabase(context: Context): HDRDatabase {
        synchronized(this) {
            return database ?: createAppDatabase(context)
        }
    }

    private fun createAppDatabase(context: Context): HDRDatabase {
        val newDb = Room.databaseBuilder(
            context,
            HDRDatabase::class.java, "hdr-database"
        ).build()
        database = newDb
        return newDb
    }

}
