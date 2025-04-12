package com.fiax.hdr.di

import android.content.Context
import androidx.room.Room
import com.fiax.hdr.data.local.HDRDatabase
import com.fiax.hdr.data.local.RoomDataSource
import com.fiax.hdr.data.repository.PatientRepository
import com.fiax.hdr.data.repository.PatientRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide the RoomDatabase as a singleton
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): HDRDatabase {
        return Room.databaseBuilder(
            context,
            HDRDatabase::class.java,
            "hdr_database"  // You can change the name as needed
        ).build()
    }

    // Provide RoomDataSource, which will now have the AppDatabase as a parameter
    @Provides
    @Singleton
    fun provideRoomDataSource(appDatabase: HDRDatabase): RoomDataSource {
        return RoomDataSource(appDatabase)
    }

    @Provides
    @Singleton
    fun providePatientRepository(roomDataSource: RoomDataSource): PatientRepository {
        return PatientRepositoryImpl(roomDataSource)
    }
}