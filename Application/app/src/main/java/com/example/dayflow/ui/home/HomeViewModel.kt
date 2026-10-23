package com.example.dayflow.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.HabitEntity
import com.example.dayflow.data.local.HabitLogEntity
import com.example.dayflow.repository.ChallengeRepository
import com.example.dayflow.repository.HabitRepository
import com.example.dayflow.utilities.persianDayOfMonth
import com.example.dayflow.utilities.persianWeekLabels
import com.example.dayflow.utilities.toPersianFullDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val today: Flow<LocalDate> = flow {
        while (true) {
            emit(LocalDate.now())
            delay(millisUntilNextDay().milliseconds)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HomeUiState> = combine(
        today,
        repository.observeHabits(),
        repository.observeLogs()
    ) { today, habits, logs ->
        buildHomeState(today, habits, logs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildHomeState(LocalDate.now(), emptyList(), emptyList())
    )




    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleHabit(habitId: Long, isDone: Boolean) {
        viewModelScope.launch {
            repository.setDone(habitId, LocalDate.now().toEpochDay(), done = !isDone)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addHabit(title: String, iconKey: String, targetDays: Int?, reminderMinutes: Int?) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.addHabit(title, iconKey, targetDays, reminderMinutes) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChallenge(
        title: String, iconKey: String, isGroup: Boolean, groupName: String?,
        durationDays: Int, startEpochDay: Long?
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            challengeRepository.create(title, iconKey, isGroup, groupName, durationDays, startEpochDay)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch { repository.deleteHabit(habitId) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun millisUntilNextDay(): Long {
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    return Duration.between(now, midnight).toMillis() + 1_000
}

private fun habitStreak(doneDays: Set<Long>, today: Long): Int {
    var day = if (today in doneDays) today else today - 1
    var count = 0
    while (day in doneDays) {
        count++
        day--
    }
    return count
}



@RequiresApi(Build.VERSION_CODES.O)
private fun buildHomeState(
    today: LocalDate,
    habits: List<HabitEntity>,
    logs: List<HabitLogEntity>
): HomeUiState {
    val todayEpoch = today.toEpochDay()
    val doneDays: Map<Long, Set<Long>> = logs
        .groupBy({ it.habitId }, { it.epochDay })
        .mapValues { it.value.toSet() }

    fun isDone(habitId: Long, day: Long) = day in doneDays[habitId].orEmpty()
    fun scheduled(day: Long) = habits.filter { it.createdAtEpochDay <= day }
    fun isComplete(day: Long): Boolean {
        val list = scheduled(day)
        return list.isNotEmpty() && list.all { isDone(it.id, day) }
    }



    val habitItems = habits.map { habit ->
        HabitUi(
            id = habit.id,
            title = habit.title,
            iconKey = habit.iconKey,
            goalText = habit.goalText,
            isDoneToday = isDone(habit.id, todayEpoch),
            streak = habitStreak(doneDays[habit.id].orEmpty(), todayEpoch),
            targetDays = habit.targetDays,
            reminderMinutes = habit.reminderMinutes,
        )
    }

    val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))
    val week = persianWeekLabels.mapIndexed { index, label ->
        val date = weekStart.plusDays(index.toLong())
        val day = date.toEpochDay()
        val status = when {
            day == todayEpoch -> DayStatus.Today
            day > todayEpoch -> DayStatus.None
            scheduled(day).isEmpty() -> DayStatus.None
            isComplete(day) -> DayStatus.Done
            else -> DayStatus.Missed
        }
        WeekDayUi(label = label, dayNumber = date.persianDayOfMonth(), status = status)
    }

    var streak = 0
    val earliest = habits.minOfOrNull { it.createdAtEpochDay }
    if (earliest != null) {
        var day = if (isComplete(todayEpoch)) todayEpoch else todayEpoch - 1
        while (day >= earliest && isComplete(day)) {
            streak++
            day--
        }
    }

    return HomeUiState(
        dateText = today.toPersianFullDate(),
        doneCount = habitItems.count { it.isDoneToday },
        totalCount = habitItems.size,
        streakDays = streak,
        week = week,
        habits = habitItems
    )


}
