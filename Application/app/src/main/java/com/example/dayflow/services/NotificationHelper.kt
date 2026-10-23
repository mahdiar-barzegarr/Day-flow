package com.example.dayflow.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dayflow.MainActivity
import com.example.dayflow.R

object NotificationHelper {
    private const val CH_NOTIF = "habit_notif"
    private const val CH_ALARM = "habit_alarm"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CH_NOTIF, "یادآوری عادت‌ها", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ch = NotificationChannel(CH_ALARM, "الارم عادت‌ها", NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(
                alarmSound,
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 800, 500, 800, 500, 800)
        }
        nm.createNotificationChannel(ch)
    }

    private fun openAppIntent(ctx: Context, id: Int) = PendingIntent.getActivity(
        ctx, id, Intent(ctx, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun showNotification(ctx: Context, id: Int, title: String, text: String) {
        val n = NotificationCompat.Builder(ctx, CH_NOTIF)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title).setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(ctx, id))
            .build()
        notify(ctx, id, n)
    }

    fun showAlarm(ctx: Context, id: Int, title: String) {
        val n = NotificationCompat.Builder(ctx, CH_ALARM)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title).setContentText("وقت انجام عادتته ⏰")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(ctx, id))
            .build()
        n.flags = n.flags or Notification.FLAG_INSISTENT // صدا تا بستن ادامه داره
        notify(ctx, id + 500_000, n)
    }

    @SuppressLint("MissingPermission")
    private fun notify(ctx: Context, id: Int, n: Notification) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        NotificationManagerCompat.from(ctx).notify(id, n)
    }
}