package com.fiax.hdr.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiax.hdr.data.bluetooth.BluetoothCustomManager
import com.fiax.hdr.data.local.HDRDatabase
import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.repository.PatientRepositoryImpl
import com.fiax.hdr.domain.repository.PatientRepository
import com.fiax.hdr.domain.usecase.InsertPatientLocallyUseCase
import com.fiax.hdr.domain.usecase.SendPatientViaBluetoothUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Define migrations
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Migration1-2: image field added for Patient entity
            db.execSQL("ALTER TABLE patients ADD COLUMN image BLOB DEFAULT NULL")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): HDRDatabase {
        return Room.databaseBuilder(
            context,
            HDRDatabase::class.java,
            "hdr_database"
        )
            .addMigrations(MIGRATION_1_2) // Add migrations here
            .build()
    }

    // Provide RoomDataSource, which will now have the AppDatabase as a parameter
    @Provides
    @Singleton
    fun provideRoomDataSource(appDatabase: HDRDatabase): RoomDataSource {
        return RoomDataSource(appDatabase)
    }

    @Provides
    @Singleton
    fun providePatientRepository(roomDataSource: RoomDataSource, bluetoothManager: BluetoothCustomManager): PatientRepository {
        return PatientRepositoryImpl(roomDataSource, bluetoothManager)
    }

    @Provides
    @Singleton
    fun provideBluetoothCustomManager(): BluetoothCustomManager {
        return BluetoothCustomManager()
    }

    @Provides
    fun provideSendPatientUseCase(
        bluetoothManager: BluetoothCustomManager
    ): SendPatientViaBluetoothUseCase {
        return SendPatientViaBluetoothUseCase(bluetoothManager)
    }

    @Provides
    fun provideInsertPatientLocallyUseCase(
        patientRepository: PatientRepository
    ): InsertPatientLocallyUseCase {
        return InsertPatientLocallyUseCase(patientRepository)
    }
}