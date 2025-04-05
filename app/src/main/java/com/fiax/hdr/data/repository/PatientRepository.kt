package com.fiax.hdr.data.repository

import com.fiax.hdr.data.local.PatientDao
import com.fiax.hdr.data.model.Patient

interface PatientRepository {
    suspend fun addPatient(patient: Patient)
}

class LocalPatientRepositoryImpl(private val patientDao: PatientDao) : PatientRepository {

    override suspend fun addPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

}