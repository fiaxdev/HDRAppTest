package com.fiax.hdr.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fiax.hdr.domain.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insertPatient(patient: Patient): Long

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients")
    fun getPatients(): Flow<List<Patient>>
}