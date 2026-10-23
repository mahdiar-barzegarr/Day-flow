package com.example.dayflow.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dayflow.data.local.HabitDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderEntryPoint {
    fun habitDao(): HabitDao
}

fun reminderDao(ctx: Context): HabitDao =
    EntryPointAccessors.fromApplication(ctx.applicationContext, ReminderEntryPoint::class.java).habitDao()

suspend fun loadReminderHabits(ctx: Context): List<ReminderHabit> =
    reminderDao(ctx).observeActiveHabits().first()
        .filter { it.reminderMinutes != null }
        .map { ReminderHabit(it.id.toInt(), it.title, it.reminderMinutes!! / 60, it.reminderMinutes!! % 60) }

@RequiresApi(Build.VERSION_CODES.O)
suspend fun hasUnfinishedToday(ctx: Context): Boolean {
    val dao = reminderDao(ctx)
    val today = LocalDate.now().toEpochDay()
    val doneIds = dao.observeAllLogs().first().filter { it.epochDay == today }.map { it.habitId }.toSet()
    return dao.observeActiveHabits().first().any { it.id !in doneIds }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun isHabitDoneToday(ctx: Context, habitId: Long): Boolean? {
    val dao = reminderDao(ctx)
    dao.observeActiveHabits().first().firstOrNull { it.id == habitId } ?: return null
    val today = LocalDate.now().toEpochDay()
    return dao.observeAllLogs().first().any { it.habitId == habitId && it.epochDay == today }
}
