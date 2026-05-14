package com.example.waterlevelcontroller.di

import android.content.Context
import androidx.room.Room
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.data.local.AppDatabase
import com.example.waterlevelcontroller.data.remote.FirebaseDataSource
import com.example.waterlevelcontroller.data.remote.LogsDataSource
import com.example.waterlevelcontroller.data.remote.PumpDataSource
import com.example.waterlevelcontroller.data.remote.ScheduleDataSource
import com.example.waterlevelcontroller.data.repository.LogsRepositoryImpl
import com.example.waterlevelcontroller.data.repository.ScheduleRepositoryImpl
import com.example.waterlevelcontroller.data.repository.WaterRepositoryImpl
import com.example.waterlevelcontroller.domain.repository.LogsRepository
import com.example.waterlevelcontroller.domain.repository.ScheduleRepository
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Rename this to differentiate from Room Database
    @Provides
    @Singleton
    fun provideRealtimeDatabaseReference(): DatabaseReference =
        FirebaseDatabase.getInstance().reference

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)

    // ROOM DATABASE PROVIDER
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "water_level_db"
        ).build()
    }

    // UPDATED LOGS REPOSITORY PROVIDER
    // You must pass the database and firestore here because LogsRepositoryImpl needs them
    @Provides
    @Singleton
    fun provideLogsRepository(
        logsDataSource: LogsDataSource,
        database: AppDatabase,
        firestore: FirebaseFirestore
    ): LogsRepository = LogsRepositoryImpl(logsDataSource, database, firestore)

    @Provides
    @Singleton
    fun provideWaterRepository(
        pumpDataSource: PumpDataSource
    ): WaterRepository = WaterRepositoryImpl(pumpDataSource)

    @Provides
    @Singleton
    fun provideScheduleRepository(
        scheduleDataSource: ScheduleDataSource
    ): ScheduleRepository = ScheduleRepositoryImpl(scheduleDataSource)
}