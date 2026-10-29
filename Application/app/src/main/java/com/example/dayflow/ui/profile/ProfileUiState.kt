package com.example.dayflow.ui.profile


data class ProfileChallengeUi(
    val id: Long,
    val title: String,
    val progress: Float
)

data class ProfileNoteUi(
    val id: Long,
    val title: String,
    val preview: String
)

data class ProfileUiState(
    val name: String = "",
    val totalHabits: Int = 0,
    val activeChallengesCount: Int = 0,
    val bio: String = "هر روز یه قدم کوچک، یه هدف بزرگ.",
    val streakDays: Int = 0,
    val totalHabitsDone: Int = 0,
    val completedChallenges: Int = 0,
    val activeChallenges: List<ProfileChallengeUi> = emptyList(),
    val themeLabel: String = "تاریک",
    val totalNotes: Int = 0,
    val recentNotes: List<ProfileNoteUi> = emptyList()
)

enum class ProfileSetting { Reminders, Privacy, Theme, Account }