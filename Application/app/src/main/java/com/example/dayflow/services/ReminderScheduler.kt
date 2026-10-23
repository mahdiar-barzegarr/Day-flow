package com.example.dayflow.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import kotlin.jvm.java

data class ReminderHabit(val id: Int, val title: String, val hour: Int, val minute: Int)

object ReminderScheduler {
    const val KIND = "kind"
    const val KIND_HABIT = "habit"
    const val KIND_DAILY = "daily"
    private const val DAILY_CODE = 999_999
    private const val DAILY_HOUR = 21

    private fun nextTime(hour: Int, minute: Int): Long {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        if (c.timeInMillis <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1)
        return c.timeInMillis
    }

    private fun pi(ctx: Context, code: Int, build: Intent.() -> Unit = {}) =
        PendingIntent.getBroadcast(
            ctx, code,
            Intent(ctx, ReminderReceiver::class.java).apply(build),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun setExact(ctx: Context, time: Long, p: PendingIntent) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            am.set(AlarmManager.RTC_WAKEUP, time, p) // fallback غیر دقیق
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, p)
        }
    }

    fun scheduleHabit(ctx: Context, h: ReminderHabit) {
        Log.d("Reminder", "scheduled ${h.title} at ${h.hour}:${h.minute} -> ${nextTime(h.hour, h.minute)}")
        val p = pi(ctx, h.id) {
            putExtra(KIND, KIND_HABIT)
            putExtra("id", h.id); putExtra("title", h.title)
            putExtra("hour", h.hour); putExtra("minute", h.minute)
        }
        setExact(ctx, nextTime(h.hour, h.minute), p)
    }

    fun scheduleDaily(ctx: Context) {
        val p = pi(ctx, DAILY_CODE) { putExtra(KIND, KIND_DAILY) }
        setExact(ctx, nextTime(DAILY_HOUR, 0), p)
    }

    fun cancelAll(ctx: Context, habits: List<ReminderHabit>) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        habits.forEach { am.cancel(pi(ctx, it.id)) }
        am.cancel(pi(ctx, DAILY_CODE))
    }

    fun cancelHabit(ctx: Context, id: Int) {
        ctx.getSystemService(AlarmManager::class.java).cancel(pi(ctx, id))
    }

    fun rescheduleAll(ctx: Context, habits: List<ReminderHabit>) {
        val prefs = NotificationPrefs(ctx)
        cancelAll(ctx, habits)
        if (prefs.alarmEnabled || prefs.notifEnabled) {
            habits.forEach { scheduleHabit(ctx, it) }
            scheduleDaily(ctx)
        }
    }
}
