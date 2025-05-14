package com.fiax.hdr.domain.model

import android.os.Parcelable
import com.fiax.hdr.utils.Gender
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Patient(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var age: Int,
    var sex: String = Gender.Unspecified.displayName,
    var village: String = "",
    var parish: String = "",
    var subCounty: String = "",
    var district: String = "",
    var nextOfKin: String = "",
    var contact: String = "",
    var image: ByteArray? = null
) : Parcelable