package com.fiax.hdr.data.repository

import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomDataSource,
    private val bluetoothCustomManager: BluetoothCustomManager
) : PatientRepository {

    private val _receivedPatients = MutableSharedFlow<Patient>()
    override val receivedPatients: SharedFlow<Patient> = _receivedPatients.asSharedFlow()

    private val _newPatientEvents = MutableSharedFlow<Patient>()
    override val newPatientEvents: SharedFlow<Patient> = _newPatientEvents.asSharedFlow()

    init {
        listenForPatients()
    }

    private fun listenForPatients() {
        // Listening for Bluetooth patients
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothCustomManager.receivedPatients.collect { patient ->
                _receivedPatients.emit(patient)
                roomDataSource.insertPatient(patient)
                _newPatientEvents.emit(patient)
            }
        }
    }

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