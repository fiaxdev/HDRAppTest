package com.fiax.hdr.domain.model

import java.nio.ByteBuffer

object PatientSerializer {

    fun serialize(patient: Patient): ByteArray {
        val nameBytes = patient.name.toByteArray(Charsets.UTF_8)
        val sexBytes = patient.sex.toByteArray(Charsets.UTF_8)
        val villageBytes = patient.village.toByteArray(Charsets.UTF_8)
        val parishBytes = patient.parish.toByteArray(Charsets.UTF_8)
        val subcountyBytes = patient.subCounty.toByteArray(Charsets.UTF_8)
        val districtBytes = patient.district.toByteArray(Charsets.UTF_8)
        val nextOfKinBytes = patient.nextOfKin.toByteArray(Charsets.UTF_8)
        val contactBytes = patient.contact.toByteArray(Charsets.UTF_8)
        val imageBytes = patient.image ?: ByteArray(0)

        val buffer = ByteBuffer.allocate(
            4 + // age
                    4 + nameBytes.size +
                    4 + sexBytes.size +
                    4 + villageBytes.size +
                    4 + parishBytes.size +
                    4 + subcountyBytes.size +
                    4 + districtBytes.size +
                    4 + nextOfKinBytes.size +
                    4 + contactBytes.size +
                    4 + imageBytes.size
        )

        buffer.putInt(patient.age)

        buffer.putInt(nameBytes.size)
        buffer.put(nameBytes)

        buffer.putInt(sexBytes.size)
        buffer.put(sexBytes)

        buffer.putInt(villageBytes.size)
        buffer.put(villageBytes)

        buffer.putInt(parishBytes.size)
        buffer.put(parishBytes)

        buffer.putInt(subcountyBytes.size)
        buffer.put(subcountyBytes)

        buffer.putInt(districtBytes.size)
        buffer.put(districtBytes)

        buffer.putInt(nextOfKinBytes.size)
        buffer.put(nextOfKinBytes)

        buffer.putInt(contactBytes.size)
        buffer.put(contactBytes)

        buffer.putInt(imageBytes.size)
        buffer.put(imageBytes)

        return buffer.array()
    }

    fun deserialize(bytes: ByteArray): Patient {
        val buffer = ByteBuffer.wrap(bytes)

        val age = buffer.int

        val name = buffer.readString()
        val sex = buffer.readString()
        val village = buffer.readString()
        val parish = buffer.readString()
        val subcounty = buffer.readString()
        val district = buffer.readString()
        val nextOfKin = buffer.readString()
        val contact = buffer.readString()

        val imageLength = buffer.int
        val image = if (imageLength > 0) {
            ByteArray(imageLength).also { buffer.get(it) }
        } else {
            null
        }

        return Patient(
            name = name,
            age = age,
            sex = sex,
            village = village,
            parish = parish,
            subCounty = subcounty,
            district = district,
            nextOfKin = nextOfKin,
            contact = contact,
            image = image
        )
    }

    private fun ByteBuffer.readString(): String {
        val length = this.int
        val bytes = ByteArray(length)
        this.get(bytes)
        return String(bytes, Charsets.UTF_8)
    }
}
