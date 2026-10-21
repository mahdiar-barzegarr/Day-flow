package com.example.dayflow.ui.profile

data class ProfileChallengeUi(val id: Long, val title: String, val progress: Float)

data class ProfileUiState(
    val name: String = "کاربر مهمان",
    val username: String = "",
    val bio: String = "هر روز یه قدم کوچک، یه هدف بزرگ.",
    val streakDays: Int = 0,
    val totalHabitsDone: Int = 0,
    val completedChallenges: Int = 0,
    val activeChallenges: List<ProfileChallengeUi> = emptyList(),
    val themeLabel: String = "تاریک",
    val isLoggedIn: Boolean = false
)

enum class ProfileSetting { Reminders, Privacy, Theme, Account }