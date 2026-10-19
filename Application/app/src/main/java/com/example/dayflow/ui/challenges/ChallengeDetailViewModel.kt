package com.example.dayflow.ui.challenges

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.ChallengeEntity
import com.example.dayflow.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ChallengeDetailUi(
    val challenge: ChallengeUi,
    val dots: List<DotStatus>,
    val daysLeft: Int
)

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ChallengeRepository
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle["id"])

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ChallengeDetailUi?> = combine(
        todayEpochDayFlow(),
        repository.observeChallenge(id),
        repository.observeLogs(id)
    ) { today, challenge, logs ->
        challenge?.let { buildDetail(it, logs.map { l -> l.epochDay }.toSet(), today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleToday(isDone: Boolean) {
        viewModelScope.launch { repository.setDone(id, LocalDate.now().toEpochDay(), !isDone) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun activate() { viewModelScope.launch { repository.activate(id) } }
    @RequiresApi(Build.VERSION_CODES.O)
    fun pause() { viewModelScope.launch { repository.pause(id) } }
    @RequiresApi(Build.VERSION_CODES.O)
    fun resume() { viewModelScope.launch { repository.resume(id) } }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.delete(id)
            onDone()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun buildDetail(c: ChallengeEntity, done: Set<Long>, today: Long): ChallengeDetailUi {
    val ui = buildChallengeUi(c, done, today)
    val start = c.startEpochDay
    val end = c.endEpochDay

    val dots = if (start == null) emptyList() else (0 until c.durationDays).map { i ->
        val day = start + i
        when {
            day in done -> DotStatus.Done
            day == today -> DotStatus.Today
            day < today -> DotStatus.Missed
            else -> DotStatus.Future
        }
    }
    val daysLeft = if (start == null || end == null) c.durationDays
    else (end - maxOf(today, start) + 1).toInt().coerceIn(0, c.durationDays)

    return ChallengeDetailUi(ui, dots, daysLeft)
}