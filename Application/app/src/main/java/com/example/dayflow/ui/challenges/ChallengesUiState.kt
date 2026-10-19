package com.example.dayflow.ui.challenges

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dayflow.data.local.ChallengeEntity
import com.example.dayflow.data.local.ChallengeStatus
import com.example.dayflow.utilities.toPersianDayMonth
import com.example.dayflow.utilities.toPersianDayMonthYear
import com.example.dayflow.utilities.toPersianDigits
import java.time.LocalDate

enum class ChallengeTab {
    Active, Upcoming, Completed;

    fun matches(s: ChallengeStatus) = when (this) {
        Active -> s == ChallengeStatus.ACTIVE || s == ChallengeStatus.PAUSED
        Upcoming -> s == ChallengeStatus.UPCOMING
        Completed -> s == ChallengeStatus.COMPLETED
    }
}

enum class TypeFilter { All, Personal, Group }
enum class DotStatus { Done, Missed, Today, Future }

data class ChallengeUi(
    val id: Long,
    val title: String,
    val iconKey: String,
    val isGroup: Boolean,
    val groupName: String?,
    val status: ChallengeStatus,
    val dateText: String,
    val doneDays: Int,
    val totalDays: Int,
    val streak: Int,
    val doneToday: Boolean
) {
    val progress: Float
        get() = if (totalDays == 0) 0f else (doneDays / totalDays.toFloat()).coerceIn(0f, 1f)
}

data class ChallengesUiState(
    val tab: ChallengeTab = ChallengeTab.Active,
    val filter: TypeFilter = TypeFilter.All,
    val query: String = "",
    val searchOpen: Boolean = false,
    val activeCount: Int = 0,
    val upcomingCount: Int = 0,
    val completedCount: Int = 0,
    val items: List<ChallengeUi> = emptyList()
)

/** از ۷ روز به بالا به هفته تبدیل می‌شه */
internal fun streakText(streak: Int): String =
    if (streak >= 7) "${(streak / 7).toPersianDigits()} هفته پیاپی"
    else "${streak.toPersianDigits()} روز پیاپی"

/** وضعیت واقعی بر اساس تاریخ امروز (بدون نوشتن در دیتابیس) */
internal fun ChallengeEntity.effectiveStatus(today: Long): ChallengeStatus {
    var s = ChallengeStatus.valueOf(status)
    val start = startEpochDay
    val end = endEpochDay
    if (s == ChallengeStatus.UPCOMING && start != null && today >= start) s = ChallengeStatus.ACTIVE
    if (s == ChallengeStatus.ACTIVE && end != null && today > end) s = ChallengeStatus.COMPLETED
    return s
}

private fun streakOf(done: Set<Long>, today: Long): Int {
    var day = if (today in done) today else today - 1
    var n = 0
    while (day in done) { n++; day-- }
    return n
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun buildChallengeUi(c: ChallengeEntity, done: Set<Long>, today: Long): ChallengeUi {
    val status = c.effectiveStatus(today)
    val start = c.startEpochDay
    val end = c.endEpochDay

    val dateText = if (start != null && end != null) {
        val s = LocalDate.ofEpochDay(start)
        val e = LocalDate.ofEpochDay(end)
        if (status == ChallengeStatus.UPCOMING) "شروع: ${s.toPersianDayMonthYear()}"
        else "${s.toPersianDayMonth()} تا ${e.toPersianDayMonthYear()}"
    } else {
        "${c.durationDays.toPersianDigits()} روز · شروع بعد از فعال‌سازی"
    }

    return ChallengeUi(
        id = c.id,
        title = c.title,
        iconKey = c.iconKey,
        isGroup = c.isGroup,
        groupName = c.groupName,
        status = status,
        dateText = dateText,
        doneDays = if (start != null && end != null) done.count { it in start..end } else 0,
        totalDays = c.durationDays,
        streak = streakOf(done, today),
        doneToday = today in done
    )
}