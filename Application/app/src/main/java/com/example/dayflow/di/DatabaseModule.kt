package com.example.dayflow.di

import android.content.Context
import androidx.room.Room
import com.example.dayflow.data.local.DayFlowDatabase
import com.example.dayflow.data.local.HabitDao
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
        Room.databaseBuilder(context, DayFlowDatabase::class.java, "dayflow.db").build()

    @Provides
    fun provideHabitDao(database: DayFlowDatabase): HabitDao = database.habitDao()
}
