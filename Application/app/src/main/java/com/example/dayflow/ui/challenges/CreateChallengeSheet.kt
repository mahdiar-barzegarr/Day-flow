package com.example.dayflow.ui.challenges

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dayflow.ui.home.SheetChip
import com.example.dayflow.ui.home.StepButton
import com.example.dayflow.ui.home.habitVisual
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDayMonth
import com.example.dayflow.utilities.toPersianDayMonthYear
import com.example.dayflow.utilities.toPersianDigits
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

private enum class StartOption(val label: String) {
    Now("همین الان"), Tomorrow("فردا"), NextSaturday("شنبه‌ی آینده"), Later("بعداً فعال می‌کنم")
}

@RequiresApi(Build.VERSION_CODES.O)
private fun StartOption.epochDay(today: LocalDate): Long? = when (this) {
    StartOption.Now -> today.toEpochDay()
    StartOption.Tomorrow -> today.plusDays(1).toEpochDay()
    StartOption.NextSaturday -> today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).toEpochDay()
    StartOption.Later -> null
}

private val challengeIconKeys = listOf("star", "book", "fitness", "water", "sleep")
private val durationPresets = listOf(7, 14, 21, 30, 66)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeSheet(
    onDismiss: () -> Unit,
    onCreate: (
        title: String, iconKey: String, isGroup: Boolean, groupName: String?,
        durationDays: Int, startEpochDay: Long?
    ) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }

    var title by remember { mutableStateOf("") }
    var iconKey by remember { mutableStateOf("star") }
    var isGroup by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(30) }
    var startOption by remember { mutableStateOf(StartOption.Now) }

    val start = startOption.epochDay(today)
    val rangeText = if (start == null) {
        "تاریخ شروع و پایان بعد از فعال‌سازی محاسبه می‌شه."
    } else {
        "${LocalDate.ofEpochDay(start).toPersianDayMonth()} تا " +
                LocalDate.ofEpochDay(start + duration - 1).toPersianDayMonthYear()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.outline) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text("چالش جدید", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)

            // پیش‌نمایش زنده با تاریخ محاسبه‌شده
            FlowSurface {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChallengeIconBox(iconKey)
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            title.ifBlank { "اسم چالشت اینجا دیده می‌شه" },
                            style = MaterialTheme.typography.titleSmall,
                            color = if (title.isBlank()) colors.onSurfaceVariant else colors.onSurface
                        )
                        Text(
                            "${duration.toPersianDigits()} روز · $rangeText",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                    TypeBadge(isGroup)
                }
            }

            LabeledSection("عنوان", "ضروری") {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 40) title = it },
                    placeholder = { Text("مثلاً: ۳۰ روز مطالعه", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = challengeFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            LabeledSection("آیکن", "اختیاری") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    challengeIconKeys.forEach { key ->
                        val (icon, tint) = habitVisual(key)
                        val selected = key == iconKey
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(tint.copy(alpha = if (selected) 0.25f else 0.1f))
                                .border(1.5.dp, if (selected) tint else Color.Transparent, RoundedCornerShape(14.dp))
                                .clickable { iconKey = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            LabeledSection("شروع از کی؟") {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StartOption.entries.forEach { option ->
                        SheetChip(option.label, startOption == option) { startOption = option }
                    }
                }
            }

            LabeledSection("چند روز؟") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationPresets.forEach { d ->
                            SheetChip("${d.toPersianDigits()} روز", duration == d) { duration = d }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        StepButton(Icons.Outlined.Remove) { duration = (duration - 1).coerceAtLeast(1) }
                        Text(
                            "${duration.toPersianDigits()} روز",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSurface
                        )
                        StepButton(Icons.Outlined.Add) { duration = (duration + 1).coerceAtMost(365) }
                    }
                }
            }

            val enabled = title.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(if (enabled) 1f else 0.35f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(extra.brandGradient)
                    .clickable(enabled = enabled) {
                        onCreate(
                            title.trim(), iconKey, isGroup,
                            groupName.takeIf { isGroup }, duration, start
                        )
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("ایجاد چالش", style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
        }
    }
}

@Composable
private fun TypeCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) colors.primary.copy(alpha = 0.18f) else colors.surfaceVariant)
            .border(1.dp, if (selected) colors.primary else colors.outline, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = if (selected) colors.onSurface else colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.onSurface else colors.onSurfaceVariant
        )
    }
}