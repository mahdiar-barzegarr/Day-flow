package com.example.dayflow.di

import android.content.Context
import androidx.room.Room
import com.example.dayflow.data.local.ChallengeDao
import com.example.dayflow.data.local.DayFlowDatabase
import com.example.dayflow.data.local.HabitDao
import com.example.dayflow.ui.group.GroupDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DayFlowDatabase =
        Room.databaseBuilder(context, DayFlowDatabase::class.java, "dayflow.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHabitDao(database: DayFlowDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideChallengeDao(database: DayFlowDatabase): ChallengeDao = database.challengeDao()

    @Provides
    @Singleton
    fun provideGroupDao(db: DayFlowDatabase): GroupDao = db.groupDao()
}
