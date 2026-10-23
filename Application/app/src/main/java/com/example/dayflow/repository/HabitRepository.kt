package com.example.dayflow.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dayflow.data.local.HabitDao
import com.example.dayflow.data.local.HabitEntity
import com.example.dayflow.data.local.HabitLogEntity
import com.example.dayflow.services.NotificationPrefs
import com.example.dayflow.services.ReminderHabit
import com.example.dayflow.services.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    @ApplicationContext private val context: Context
) {

    fun observeHabits(): Flow<List<HabitEntity>> = habitDao.observeActiveHabits()

    fun observeLogs(): Flow<List<HabitLogEntity>> = habitDao.observeAllLogs()

    fun observeTotalDone(): Flow<Int> = habitDao.observeTotalDone()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addHabit(
        title: String,
        iconKey: String = "star",
        targetDays: Int? = null,
        reminderMinutes: Int? = null
    ): Long {
        val id = habitDao.insertHabit(
            HabitEntity(
                title = title.trim(),
                iconKey = iconKey,
                targetDays = targetDays,
                reminderMinutes = reminderMinutes,
                createdAtEpochDay = LocalDate.now().toEpochDay()
            )
        )

        val prefs = NotificationPrefs(context)
        Log.d("Reminder", "habit saved: $title, minutes=$reminderMinutes, alarm=${prefs.alarmEnabled}, notif=${prefs.notifEnabled}")

        if (reminderMinutes != null && (prefs.alarmEnabled || prefs.notifEnabled)) {
            ReminderScheduler.scheduleHabit(
                context,
                ReminderHabit(id.toInt(), title.trim(), reminderMinutes / 60, reminderMinutes % 60)
            )
        }
        return id
    }

    suspend fun setDone(habitId: Long, epochDay: Long, done: Boolean) {
        if (done) {
            habitDao.insertLog(HabitLogEntity(habitId, epochDay))
        } else {
            habitDao.deleteLog(habitId, epochDay)
        }
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.archiveHabit(habitId)
        ReminderScheduler.cancelHabit(context, habitId.toInt())
    }
}