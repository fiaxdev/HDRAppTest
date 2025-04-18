package com.fiax.hdr.data.repository

import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.utils.Resource
import javax.inject.Inject

interface PatientRepository {
    suspend fun addPatient(patient: Patient): Resource<Unit>
}

class PatientRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomDataSource
) : PatientRepository {

    override suspend fun addPatient(patient: Patient): Resource<Unit> {
        return try {
            roomDataSource.insertPatient(patient)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

}