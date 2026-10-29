package com.example.dayflow.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.*
import com.example.dayflow.data.local.Token.TokenManager
import com.example.dayflow.di.NotesRepository
import com.example.dayflow.repository.ChallengeRepository
import com.example.dayflow.repository.HabitRepository
import com.example.dayflow.ui.challenges.buildChallengeUi
import com.example.dayflow.ui.challenges.effectiveStatus
import com.example.dayflow.ui.challenges.todayEpochDayFlow
import com.example.dayflow.utilities.overallStreak
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private data class HabitStats(val streak: Int, val totalDone: Int, val count: Int)
private data class ChallengeStats(val completed: Int, val activeCount: Int, val active: List<ProfileChallengeUi>)
private data class NoteStats(val total: Int, val recent: List<ProfileNoteUi>)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    habitRepository: HabitRepository,
    challengeRepository: ChallengeRepository,
    notesRepository: NotesRepository,
    private val userPrefs: UserPreferences
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val todayFlow = todayEpochDayFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val habitStats = combine(
        habitRepository.observeHabits(),
        habitRepository.observeLogs(),
        habitRepository.observeTotalDone(),
        todayFlow
    ) { habits, logs, total, day ->
        HabitStats(streak = overallStreak(habits, logs, day), totalDone = total, count = habits.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val challengeStats = combine(
        challengeRepository.observeChallenges(),
        challengeRepository.observeAllLogs(),
        todayFlow
    ) { challenges, logs, day -> buildChallengeStats(challenges, logs, day) }

    private val noteStats = notesRepository.getAllNotes().map { notes ->
        NoteStats(
            total = notes.size,
            recent = notes
                .sortedByDescending { it.updatedAt }
                .take(3)
                .map {
                    ProfileNoteUi(
                        id = it.id,
                        title = it.title.ifBlank { "بدون عنوان" },
                        preview = it.content.take(80)
                    )
                }
        )
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            userPrefs.saveProfile(name.trim(), "", uiState.value.bio)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ProfileUiState> = combine(
        userPrefs.profile, habitStats, challengeStats, noteStats
    ) { profile, habits, challenges, notes ->
        ProfileUiState(
            name = profile.name,
            bio = profile.bio,
            streakDays = habits.streak,
            totalHabitsDone = habits.totalDone,
            completedChallenges = challenges.completed,
            activeChallenges = challenges.active,
            totalNotes = notes.total,
            recentNotes = notes.recent,
            totalHabits = habits.count,
            activeChallengesCount = challenges.activeCount,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun saveProfile(name: String, username: String, bio: String) {
        viewModelScope.launch { userPrefs.saveProfile(name, username, bio) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun buildChallengeStats(
    challenges: List<ChallengeEntity>,
    logs: List<ChallengeLogEntity>,
    today: Long
): ChallengeStats {
    val doneById = logs.groupBy({ it.challengeId }, { it.epochDay }).mapValues { it.value.toSet() }
    val withStatus = challenges.map { it to it.effectiveStatus(today) }

    return ChallengeStats(
        activeCount = withStatus.count { it.second == ChallengeStatus.ACTIVE },
        completed = withStatus.count { it.second == ChallengeStatus.COMPLETED },
        active = withStatus
            .filter { it.second == ChallengeStatus.ACTIVE }
            .take(3)
            .map { (c, _) ->
                val ui = buildChallengeUi(c, doneById[c.id].orEmpty(), today)
                ProfileChallengeUi(ui.id, ui.title, ui.progress)
            }
    )
}