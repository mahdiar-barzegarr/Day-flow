package com.example.dayflow.ui.home

enum class DayStatus { Done, Missed, Today, None }

data class WeekDayUi(
    val label: String,
    val dayNumber: Int,
    val status: DayStatus
)

data class HabitUi(
    val id: Long,
    val title: String,
    val iconKey: String,
    val goalText: String?,
    val isDoneToday: Boolean,
    val streak: Int
)

data class HomeUiState(
    val userName: String = "",
    val dateText: String = "",
    val doneCount: Int = 0,
    val totalCount: Int = 0,
    val streakDays: Int = 0,
    val week: List<WeekDayUi> = emptyList(),
    val habits: List<HabitUi> = emptyList()
) {
    val progress: Float
        get() = if (totalCount == 0) 0f else doneCount / totalCount.toFloat()
}
