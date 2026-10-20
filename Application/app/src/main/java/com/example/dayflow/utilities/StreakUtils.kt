package com.example.dayflow.utilities

import com.example.dayflow.data.local.HabitEntity
import com.example.dayflow.data.local.HabitLogEntity

/** تعداد روزهای پیاپی که «همه‌ی» عادت‌ها انجام شدن (اگه امروز کامل نیست، از دیروز می‌شمره) */
fun overallStreak(habits: List<HabitEntity>, logs: List<HabitLogEntity>, today: Long): Int {
    val earliest = habits.minOfOrNull { it.createdAtEpochDay } ?: return 0
    val doneByDay = logs.groupBy({ it.epochDay }, { it.habitId }).mapValues { it.value.toSet() }

    fun isComplete(day: Long): Boolean {
        val scheduled = habits.filter { it.createdAtEpochDay <= day }
        return scheduled.isNotEmpty() && scheduled.all { it.id in doneByDay[day].orEmpty() }
    }

    var day = if (isComplete(today)) today else today - 1
    var streak = 0
    while (day >= earliest && isComplete(day)) {
        streak++
        day--
    }
    return streak
}