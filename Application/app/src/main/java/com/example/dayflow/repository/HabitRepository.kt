package com.example.dayflow.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.updateAll
import com.example.dayflow.data.local.HabitDao
import com.example.dayflow.data.local.HabitEntity
import com.example.dayflow.data.local.HabitLogEntity
import com.example.dayflow.services.NotificationPrefs
import com.example.dayflow.services.ReminderHabit
import com.example.dayflow.services.ReminderScheduler
import com.example.dayflow.ui.widget.HabitsWidget
import com.example.dayflow.ui.widget.WidgetHabit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        Log.d(
            "Reminder",
            "habit saved: $title, minutes=$reminderMinutes, alarm=${prefs.alarmEnabled}, notif=${prefs.notifEnabled}"
        )

        if (reminderMinutes != null && (prefs.alarmEnabled || prefs.notifEnabled)) {
            ReminderScheduler.scheduleHabit(
                context,
                ReminderHabit(id.toInt(), title.trim(), reminderMinutes / 60, reminderMinutes % 60)
            )
        }
        HabitsWidget().updateAll(context)
        return id
    }

    suspend fun setDone(habitId: Long, epochDay: Long, done: Boolean) {
        if (done) habitDao.insertLog(HabitLogEntity(habitId, epochDay))
        else habitDao.deleteLog(habitId, epochDay)
        HabitsWidget().updateAll(context)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.archiveHabit(habitId)
        ReminderScheduler.cancelHabit(context, habitId.toInt())
        HabitsWidget().updateAll(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeTodayWidget(): Flow<List<WidgetHabit>> =
        combine(observeHabits(), observeLogs()) { habits, logs ->
            val today = LocalDate.now().toEpochDay()
            val doneIds = logs.filter { it.epochDay == today }.map { it.habitId }.toSet()
            habits.map { WidgetHabit(it.id, it.title, it.id in doneIds) }
        }

    private val toggleMutex = Mutex()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun toggleToday(habitId: Long) = toggleMutex.withLock {
        val today = LocalDate.now().toEpochDay()
        val isDone = observeLogs().first().any { it.habitId == habitId && it.epochDay == today }
        setDone(habitId, today, !isDone)
    }
}