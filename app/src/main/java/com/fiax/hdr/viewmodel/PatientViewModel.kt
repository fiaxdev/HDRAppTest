package com.fiax.hdr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fiax.hdr.data.model.Patient
import com.fiax.hdr.data.repository.PatientRepository
import com.fiax.hdr.di.ServiceLocator

class PatientViewModel(application: Application): AndroidViewModel(application) {

    private val patientRepository: PatientRepository = ServiceLocator.providePatientRepository(application.applicationContext)

    suspend fun addPatient(patient: Patient) {
        patientRepository.addPatient(patient)
    }

}