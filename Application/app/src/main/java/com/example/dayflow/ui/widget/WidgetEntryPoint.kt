package com.example.dayflow.base

import android.content.Context
import com.example.dayflow.repository.HabitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun habitRepository(): HabitRepository
}

fun Context.habitRepo(): HabitRepository =
    EntryPointAccessors.fromApplication(applicationContext, WidgetEntryPoint::class.java)
        .habitRepository()