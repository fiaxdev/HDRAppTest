package com.fiax.hdr.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "patients")
data class Patient (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "age") var age: Int,
    @ColumnInfo(name = "sex") var sex: String,
    @ColumnInfo(name = "village") var village: String,
    @ColumnInfo(name = "parish") var parish: String,
    @ColumnInfo(name = "sub_county") var subCounty: String,
    @ColumnInfo(name = "district") var district: String,
    @ColumnInfo(name = "next_of_kin") var nextOfKin: String,
    @ColumnInfo(name = "contact") var contact: String,
)