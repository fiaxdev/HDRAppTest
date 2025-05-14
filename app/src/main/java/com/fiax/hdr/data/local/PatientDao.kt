package com.fiax.hdr.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fiax.hdr.data.model.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    @Query("SELECT * FROM patients")
    fun getPatients(): Flow<List<PatientEntity>>
}