package com.example.waterlevelcontroller.di

import android.content.Context
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.data.remote.FirebaseDataSource
import com.example.waterlevelcontroller.data.remote.PumpDataSource
import com.example.waterlevelcontroller.data.remote.ScheduleDataSource
import com.example.waterlevelcontroller.data.repository.ScheduleRepositoryImpl
import com.example.waterlevelcontroller.data.repository.WaterRepositoryImpl
import com.example.waterlevelcontroller.domain.repository.ScheduleRepository
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(): DatabaseReference =
        FirebaseDatabase.getInstance().reference

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        db: DatabaseReference
    ) = FirebaseDataSource(db)

//        @Provides
//        @Singleton
//        fun provideRepository(
//            firebase: FirebaseDataSource
//        ): WaterRepository = WaterRepositoryImpl(firebase)

    @Provides
    @Singleton
    fun providePumpDataSource(
        pumpDataSource : PumpDataSource
    ) : WaterRepository = WaterRepositoryImpl(pumpDataSource)

    @Provides
    @Singleton
    fun provideScheduleDataSource(
        scheduleDataSource: ScheduleDataSource
    ) : ScheduleRepository = ScheduleRepositoryImpl(scheduleDataSource)


    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }


}