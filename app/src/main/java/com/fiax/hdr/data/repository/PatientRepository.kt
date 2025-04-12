package com.fiax.hdr.data.repository

import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.model.Patient
import javax.inject.Inject

interface PatientRepository {
    suspend fun addPatient(patient: Patient)
}

class PatientRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomDataSource
) : PatientRepository {

    override suspend fun addPatient(patient: Patient) {
        roomDataSource.insertPatient(patient)
    }

}