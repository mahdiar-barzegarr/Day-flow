package com.example.dayflow.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.ChallengeEntity
import com.example.dayflow.data.local.ChallengeLogEntity
import com.example.dayflow.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/** امروز؛ با گذر از نیمه‌شب خودش به‌روز می‌شه. */
internal fun todayEpochDayFlow(): Flow<Long> = flow {
    while (true) {
        emit(LocalDate.now().toEpochDay())
        val now = LocalDateTime.now()
        delay(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).toMillis() + 1_000)
    }
}

private data class Controls(
    val tab: ChallengeTab = ChallengeTab.Active,
    val filter: TypeFilter = TypeFilter.All,
    val query: String = "",
    val searchOpen: Boolean = false
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val repository: ChallengeRepository
) : ViewModel() {

    private val controls = MutableStateFlow(Controls())

    val uiState: StateFlow<ChallengesUiState> = combine(
        todayEpochDayFlow(),
        repository.observeChallenges(),
        repository.observeAllLogs(),
        controls
    ) { today, challenges, logs, ctl -> buildListState(today, challenges, logs, ctl) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChallengesUiState())

    fun setTab(tab: ChallengeTab) = controls.update { it.copy(tab = tab) }
    fun setFilter(filter: TypeFilter) = controls.update { it.copy(filter = filter) }
    fun setQuery(query: String) = controls.update { it.copy(query = query) }
    fun toggleSearch() = controls.update { it.copy(searchOpen = !it.searchOpen, query = "") }

    fun createChallenge(
        title: String, iconKey: String, isGroup: Boolean, groupName: String?,
        durationDays: Int, startEpochDay: Long?
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.create(title, iconKey, isGroup, groupName, durationDays, startEpochDay)
        }
    }

    fun activate(id: Long) {
        viewModelScope.launch { repository.activate(id) }
    }
}

/** ي/ك عربی، ارقام فارسی و عربی و حروف بزرگ رو یکسان می‌کنه تا جستجو دقیق باشه */
private fun String.normalized(): String = trim().lowercase()
    .replace('ي', 'ی').replace('ك', 'ک')
    .map {
        when (it) {
            in '۰'..'۹' -> '0' + (it - '۰')
            in '٠'..'٩' -> '0' + (it - '٠')
            else -> it
        }
    }
    .joinToString("")

private fun buildListState(
    today: Long,
    challenges: List<ChallengeEntity>,
    logs: List<ChallengeLogEntity>,
    ctl: Controls
): ChallengesUiState {
    val doneById = logs.groupBy({ it.challengeId }, { it.epochDay }).mapValues { it.value.toSet() }
    val withStatus = challenges.map { it to it.effectiveStatus(today) }
    val query = ctl.query.normalized()

    val items = withStatus
        .filter { (c, status) ->
            ctl.tab.matches(status) &&
                    when (ctl.filter) {
                        TypeFilter.All -> true
                        TypeFilter.Personal -> !c.isGroup
                        TypeFilter.Group -> c.isGroup
                    } &&
                    (query.isEmpty() ||
                            c.title.normalized().contains(query) ||
                            c.groupName?.normalized()?.contains(query) == true)
        }
        .map { (c, _) -> buildChallengeUi(c, doneById[c.id].orEmpty(), today) }

    return ChallengesUiState(
        tab = ctl.tab,
        filter = ctl.filter,
        query = ctl.query,
        searchOpen = ctl.searchOpen,
        activeCount = withStatus.count { ChallengeTab.Active.matches(it.second) },
        upcomingCount = withStatus.count { ChallengeTab.Upcoming.matches(it.second) },
        completedCount = withStatus.count { ChallengeTab.Completed.matches(it.second) },
        items = items
    )
}