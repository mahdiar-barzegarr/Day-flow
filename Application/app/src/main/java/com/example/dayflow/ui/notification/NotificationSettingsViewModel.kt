package com.example.dayflow.ui.notification

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.HabitDao
import com.example.dayflow.services.NotificationPrefs
import com.example.dayflow.services.ReminderHabit
import com.example.dayflow.services.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val habitDao: HabitDao
) : ViewModel() {

    private val prefs = NotificationPrefs(ctx)

    var alarmOn by mutableStateOf(prefs.alarmEnabled)
        private set

    var notifOn by mutableStateOf(prefs.notifEnabled)
        private set

    fun setAlarm(value: Boolean) {
        alarmOn = value
        prefs.alarmEnabled = value
        reschedule()
    }

    fun setNotif(value: Boolean) {
        notifOn = value
        prefs.notifEnabled = value
        reschedule()
    }

    private fun reschedule() {
        viewModelScope.launch(Dispatchers.IO) {
            val habits = habitDao.observeActiveHabits().first()
                .filter { it.reminderMinutes != null }
                .map {
                    ReminderHabit(
                        it.id.toInt(),
                        it.title,
                        it.reminderMinutes!! / 60,
                        it.reminderMinutes!! % 60
                    )
                }
            ReminderScheduler.rescheduleAll(ctx, habits)
        }
    }
}