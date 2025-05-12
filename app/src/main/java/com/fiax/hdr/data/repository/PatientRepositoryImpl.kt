package com.fiax.hdr.data.repository

import android.util.Log
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomDataSource,
    private val bluetoothCustomManager: BluetoothCustomManager,
    private val applicationScope: CoroutineScope
) : PatientRepository {

    private val _receivedPatients = MutableSharedFlow<List<Patient>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val receivedPatients: SharedFlow<List<Patient>> = _receivedPatients.asSharedFlow()

    private val _newPatientEvents = MutableSharedFlow<Patient>()
    override val newPatientEvents: SharedFlow<Patient> = _newPatientEvents.asSharedFlow()

    init {
        listenForPatients()
    }

    private fun listenForPatients() {
        // Listening for Bluetooth patients
        applicationScope.launch(Dispatchers.IO) {
            bluetoothCustomManager.receivedPatients.collect { patient ->
                var result = addPatient(patient)
                if (result is Resource.Error) {
                    // Try once more
                    result = addPatient(patient)
                    if (result is Resource.Error) {
                        // Failed twice, log the error
                        return@collect
                    }
                }
                val currentPatients = _receivedPatients.firstOrNull() ?: emptyList()

                val updatedPatients = emptyList<Patient>() + patient + currentPatients

                // Emit the updated list
                _receivedPatients.emit(updatedPatients)
                Log.d("PatientRepositoryImpl", "Received patient: $patient")
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

    override suspend fun removePatientFromRecentlyReceived(patient: Patient) {
        val currentPatients = _receivedPatients.firstOrNull() ?: emptyList()

        val updatedPatients = currentPatients - patient

        _receivedPatients.emit(updatedPatients)
    }

    override fun getPatients(): Flow<Resource<List<Patient>>> = flow {
        emit(Resource.Loading())
        roomDataSource.getPatients().collect {
            emit(Resource.Success(it.asReversed()))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
    }
}