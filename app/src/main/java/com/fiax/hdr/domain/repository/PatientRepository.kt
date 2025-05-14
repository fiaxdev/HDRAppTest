package com.fiax.hdr.domain.repository

import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PatientRepository {
    val newPatientEvents: SharedFlow<Patient>
    val receivedPatients: StateFlow<List<Patient>>
    suspend fun addPatient(patient: Patient): Resource<Unit>
    suspend fun removePatientFromRecentlyReceived(patient: Patient)
    fun getPatients(): Flow<Resource<List<Patient>>>
}