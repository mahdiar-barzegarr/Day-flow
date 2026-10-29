package com.example.dayflow.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dayflow.di.MutePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(ctx: Context, intent: Intent) {
        if (MutePrefs.isMuted(ctx)) return
        val prefs = NotificationPrefs(ctx)
        Log.d("Reminder", "received ${intent.getStringExtra(ReminderScheduler.KIND)}")
        NotificationHelper.createChannels(ctx)

        when (intent.getStringExtra(ReminderScheduler.KIND)) {
            ReminderScheduler.KIND_HABIT -> {
                val h = ReminderHabit(
                    intent.getIntExtra("id", 0), intent.getStringExtra("title") ?: "",
                    intent.getIntExtra("hour", 0), intent.getIntExtra("minute", 0)
                )
                val pending = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val done = isHabitDoneToday(ctx, h.id.toLong())
                        if (done != null) {
                            if (!done) {
                                if (prefs.alarmEnabled) NotificationHelper.showAlarm(ctx, h.id, h.title)
                                if (prefs.notifEnabled) NotificationHelper.showNotification(ctx, h.id, h.title, "وقت انجام عادتته")
                            }
                            ReminderScheduler.scheduleHabit(ctx, h)
                        }
                    } finally { pending.finish() }
                }
            }

            ReminderScheduler.KIND_DAILY -> {
                val pending = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (hasUnfinishedToday(ctx)) {
                            NotificationHelper.showNotification(
                                ctx, 999_999, "Day Flow",
                                "امروز یادت رفت عادت‌هات رو انجام بدی 😅"
                            )
                        }
                    } finally {
                        ReminderScheduler.scheduleDaily(ctx)
                        pending.finish()
                    }
                }
            }
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderScheduler.rescheduleAll(ctx, loadReminderHabits(ctx))
            } finally { pending.finish() }
        }
    }
}