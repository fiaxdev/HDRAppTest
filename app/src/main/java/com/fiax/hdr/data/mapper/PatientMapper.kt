package com.fiax.hdr.data.mapper

import com.fiax.hdr.data.model.PatientEntity
import com.fiax.hdr.domain.model.Patient

fun PatientEntity.toPatient(): Patient {
    return Patient(
        id = id,
        name = name,
        age = age,
        sex = sex,
        village = village,
        parish = parish,
        subCounty = subCounty,
        district = district,
        nextOfKin = nextOfKin,
        contact = contact,
        image = image
    )
}

fun Patient.toEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        name = name,
        age = age,
        sex = sex,
        village = village,
        parish = parish,
        subCounty = subCounty,
        district = district,
        nextOfKin = nextOfKin,
        contact = contact,
        image = image
    )
}
