package com.fiax.hdr.data.repository

import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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

    override fun getPatients(): Flow<Resource<List<Patient>>> = flow {
        emit(Resource.Loading())
        roomDataSource.getPatients().collect {
            emit(Resource.Success(it))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
    }
}