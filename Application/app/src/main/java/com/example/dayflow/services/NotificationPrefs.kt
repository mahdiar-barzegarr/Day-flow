package com.example.dayflow.services

import android.content.Context

class NotificationPrefs(context: Context) {
    private val sp = context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE)
    var alarmEnabled: Boolean
        get() = sp.getBoolean("alarm", false)
        set(v) = sp.edit().putBoolean("alarm", v).apply()
    var notifEnabled: Boolean
        get() = sp.getBoolean("notif", false)
        set(v) = sp.edit().putBoolean("notif", v).apply()
}