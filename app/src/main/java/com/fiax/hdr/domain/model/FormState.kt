package com.fiax.hdr.domain.model

data class FormState(
    val name: String = "",
    val age: String = "",
    val sex: String = "",
    val village: String = "",
    val parish: String = "",
    val subCounty: String = "",
    val district: String = "",
    val nextOfKin: String = "",
    val contact: String = "",
//    val errors: FormErrors = FormErrors()
)

data class FormErrors(
    val nameError: String? = null,
    val ageError: String? = null,
    val sexError: String? = null,
    val villageError: String? = null,
    val parishError: String? = null,
    val subCountyError: String? = null,
    val districtError: String? = null,
    val nextOfKinError: String? = null,
    val contactError: String? = null,
)