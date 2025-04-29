package com.fiax.hdr.data.local

import com.fiax.hdr.domain.model.Patient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    private val appDatabase: HDRDatabase
) {
    private val patientDao: PatientDao get() = appDatabase.patientDao()

    // You can add other methods that directly interact with DAOs here
    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    fun getPatients(): Flow<List<Patient>> {
        return patientDao.getPatients()
    }
}