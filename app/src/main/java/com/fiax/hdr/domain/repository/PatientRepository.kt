package com.fiax.hdr.domain.repository

import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface PatientRepository {
    val newPatientEvents: SharedFlow<Patient>
    val receivedPatients: SharedFlow<Patient>
    suspend fun addPatient(patient: Patient): Resource<Unit>
    fun getPatients(): Flow<Resource<List<Patient>>>
}