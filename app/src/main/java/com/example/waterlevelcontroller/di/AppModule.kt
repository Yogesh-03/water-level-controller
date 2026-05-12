package com.example.waterlevelcontroller.di

import android.content.Context
import com.example.waterlevelcontroller.core.network.NetworkMonitor
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

    // 1. Correct Realtime Database Provider
    @Provides
    @Singleton
    fun provideDatabase(): DatabaseReference =
        FirebaseDatabase.getInstance().reference

    // 2. Correct Firestore Provider (Removed the parameter to stop recursion)
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    // 3. Network Monitor
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)

    // 4. Repositories
    // NOTE: Ensure your Impl classes (like LogsRepositoryImpl)
    // have @Inject constructor(private val dataSource: LogsDataSource)

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

    @Provides
    @Singleton
    fun provideLogsRepository(
        logsDataSource: LogsDataSource
    ): LogsRepository = LogsRepositoryImpl(logsDataSource)
}