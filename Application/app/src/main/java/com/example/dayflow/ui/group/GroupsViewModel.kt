package com.example.dayflow.ui.group

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.*
import com.example.dayflow.data.local.Token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class GroupUi(
    val id: Long,
    val name: String,
    val privacy: GroupPrivacy,
    val icon: GroupIcon,
    val memberCount: Int,
    val memberInitials: List<String>,
    val progress: Float,
    val nextEventText: String?
)

data class InviteUi(
    val id: Long,
    val inviterName: String,
    val groupName: String
)

data class GroupsUiState(
    val isLoggedIn: Boolean = false,
    val query: String = "",
    val groups: List<GroupUi> = emptyList(),
    val totalGroups: Int = 0,
    val invites: List<InviteUi> = emptyList()
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val userPrefs: UserPreferences,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val isLoggedIn = tokenManager.accessTokenFlow
        .map { !it.isNullOrBlank() }
        .distinctUntilChanged()

    private val query = MutableStateFlow("")

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    val uiState: StateFlow<GroupsUiState> = combine(
        isLoggedIn,
        repository.observeGroups(),
        repository.observeInvites(),
        query
    ) { loggedIn, groups, invites, q ->
        val filtered = if (q.isBlank()) groups
        else groups.filter { it.name.contains(q.trim(), ignoreCase = true) }

        GroupsUiState(
            isLoggedIn = loggedIn,
            query = q,
            groups = filtered.map { it.toUi() },
            totalGroups = groups.size,
            invites = invites.map { InviteUi(it.id, it.inviterName, it.groupName) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GroupsUiState())

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun createGroup(name: String, privacy: GroupPrivacy, color: Color, icon: GroupIcon) {
        if (name.isBlank()) {
            _message.value = "اسم گروه را وارد کن."
            return
        }
        viewModelScope.launch {
            val creator = userPrefs.profile.first().name.ifBlank { "من" }
            repository.createGroup(name, privacy, creator)
            _message.value = "گروه ساخته شد."
        }
    }

    fun joinByCode(code: String) {
        if (code.isBlank()) {
            _message.value = "کد دعوت را وارد کن."
            return
        }
        // TODO: وقتی سرور آماده شد، اینجا کد رو به API بفرست
        _message.value = "پیوستن با کد دعوت به‌زودی فعال می‌شود."
    }

    fun acceptInvite(id: Long) {
        viewModelScope.launch { repository.acceptInvite(id) }
    }

    fun rejectInvite(id: Long) {
        viewModelScope.launch { repository.rejectInvite(id) }
    }

    fun clearMessage() {
        _message.value = null
    }
}

// ───────── تبدیل Entity به UI ─────────

private fun GroupEntity.toUi() = GroupUi(
    id = id,
    name = name,
    privacy = privacy,
    icon = icon,
    memberCount = memberCount,
    memberInitials = memberNames.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.first().toString() },
    progress = weeklyProgress.coerceIn(0f, 1f),
    nextEventText = if (nextEventAt != null && !nextEventTitle.isNullOrBlank())
        "${formatEventLabel(nextEventAt)} · $nextEventTitle" else null
)

private fun formatEventLabel(millis: Long, now: Long = System.currentTimeMillis()): String {
    val event = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance().apply { timeInMillis = now }

    fun Calendar.dayKey() = get(Calendar.YEAR) * 1000 + get(Calendar.DAY_OF_YEAR)
    val diff = event.dayKey() - today.dayKey()

    val dayLabel = when {
        diff == 0 -> "امروز"
        diff == 1 -> "فردا"
        else -> when (event.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "شنبه"
            Calendar.SUNDAY -> "یکشنبه"
            Calendar.MONDAY -> "دوشنبه"
            Calendar.TUESDAY -> "سه‌شنبه"
            Calendar.WEDNESDAY -> "چهارشنبه"
            Calendar.THURSDAY -> "پنجشنبه"
            else -> "جمعه"
        }
    }
    val time = "%02d:%02d".format(event.get(Calendar.HOUR_OF_DAY), event.get(Calendar.MINUTE))
    return "$dayLabel، ${time.toFaDigits()}"
}

private fun String.toFaDigits(): String = map { c ->
    if (c in '0'..'9') ('۰' + (c - '0')) else c
}.joinToString("")
