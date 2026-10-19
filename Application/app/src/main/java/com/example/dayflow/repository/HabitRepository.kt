package com.example.dayflow.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dayflow.data.local.HabitDao
import com.example.dayflow.data.local.HabitEntity
import com.example.dayflow.data.local.HabitLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {
    fun observeHabits(): Flow<List<HabitEntity>> = habitDao.observeActiveHabits()

    fun observeLogs(): Flow<List<HabitLogEntity>> = habitDao.observeAllLogs()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addHabit(
        title: String,
        iconKey: String = "star",
        targetDays: Int? = null,
        reminderMinutes: Int? = null
    ): Long = habitDao.insertHabit(
        HabitEntity(
            title = title.trim(),
            iconKey = iconKey,
            targetDays = targetDays,
            reminderMinutes = reminderMinutes,
            createdAtEpochDay = LocalDate.now().toEpochDay()
        )
    )

    suspend fun setDone(habitId: Long, epochDay: Long, done: Boolean) {
        if (done) {
            habitDao.insertLog(HabitLogEntity(habitId, epochDay))
        } else {
            habitDao.deleteLog(habitId, epochDay)
        }
    }
}
