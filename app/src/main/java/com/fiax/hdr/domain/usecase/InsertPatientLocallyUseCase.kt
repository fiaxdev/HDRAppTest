package com.fiax.hdr.domain.usecase

import com.fiax.hdr.domain.model.Patient
import com.fiax.hdr.domain.repository.PatientRepository
import javax.inject.Inject

class InsertPatientLocallyUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    suspend operator fun invoke(patient: Patient) {
        patientRepository.addPatient(patient)
    }
}
