package com.fiax.hdr.domain.repository

import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    suspend fun addPatient(patient: Patient): Resource<Unit>
    fun getPatients(): Flow<Resource<List<Patient>>>
}