package com.example.dayflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.dayflow.utilities.toPersianDigits

private val ItemHeight = 44.dp
private const val VisibleRows = 5

private fun Int.toTwoDigitsFa() =
    toString().padStart(2, '0').map { '۰' + (it - '0') }.joinToString("")

private val hourLabels = (1..12).map { it.toPersianDigits() }
private val minuteLabels = (0..59).map { it.toTwoDigitsFa() }
private val periodLabels = listOf("AM", "PM")

/**
 * انتخاب‌گر ساعت چرخشی.
 * initialMinutes و خروجی onConfirm هر دو «دقیقه از ۰۰:۰۰» هستن (مثلاً ۱۹:۳۰ = ۱۱۵۰).
 */
@Composable
fun WheelTimePickerDialog(
    initialMinutes: Int?,
    onDismiss: () -> Unit,
    onConfirm: (minutesOfDay: Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val initial = initialMinutes ?: (8 * 60)
    val initialHour24 = initial / 60
    val initialHourIndex = (if (initialHour24 % 12 == 0) 12 else initialHour24 % 12) - 1
    val initialMinute = initial % 60
    val initialPeriod = if (initialHour24 >= 12) 1 else 0

    var hourIndex by remember { mutableStateOf(initialHourIndex) }
    var minute by remember { mutableStateOf(initialMinute) }
    var period by remember { mutableStateOf(initialPeriod) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceVariant,
        title = { Text("ساعت انجام", style = MaterialTheme.typography.titleMedium) },
        text = {
            // ترتیب ستون‌ها همیشه ساعت : دقیقه AM/PM (چپ به راست)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "${hourLabels[hourIndex]}:${minuteLabels[minute]} ${periodLabels[period]}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Wheel(hourLabels, initialHourIndex, loop = true, onSelected = { hourIndex = it }, modifier = Modifier.weight(1f))
                        Text(":", style = MaterialTheme.typography.headlineMedium, color = colors.onSurface)
                        Wheel(minuteLabels, initialMinute, loop = true, onSelected = { minute = it }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Wheel(periodLabels, initialPeriod, loop = false, onSelected = { period = it }, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val hour24 = (hourIndex + 1) % 12 + if (period == 1) 12 else 0
                onConfirm(hour24 * 60 + minute)
            }) { Text("تأیید") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("لغو") } }
    )
}

@Composable
private fun Wheel(
    values: List<String>,
    initialIndex: Int,
    loop: Boolean,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val n = values.size
    val itemCount = if (loop) Int.MAX_VALUE else n
    val startIndex = if (loop) (Int.MAX_VALUE / 2 / n) * n + initialIndex else initialIndex

    val state = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val fling = rememberSnapFlingBehavior(lazyListState = state)
    val itemPx = with(LocalDensity.current) { ItemHeight.toPx() }
    val haptic = LocalHapticFeedback.current

    // ردیفی که الان وسط ستونه
    val centered by remember {
        derivedStateOf {
            state.firstVisibleItemIndex + if (state.firstVisibleItemScrollOffset > itemPx / 2) 1 else 0
        }
    }

    LaunchedEffect(state) {
        var first = true
        snapshotFlow { centered }.collect {
            onSelected(it % n)
            if (!first) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            first = false
        }
    }

    val fade = colors.surfaceVariant
    Box(modifier = modifier.height(ItemHeight * VisibleRows)) {
        // نوار هایلایت ردیف وسط
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ItemHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.primary.copy(alpha = 0.18f))
        )

        LazyColumn(
            state = state,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = ItemHeight * 2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = itemCount) { index ->
                val selected = index == centered
                Box(
                    modifier = Modifier.fillMaxWidth().height(ItemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = values[index % n],
                        style = if (selected) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.bodyLarge,
                        color = if (selected) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // محو شدن بالا و پایین ستون
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ItemHeight * 2)
                .background(Brush.verticalGradient(listOf(fade, fade.copy(alpha = 0f))))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(ItemHeight * 2)
                .background(Brush.verticalGradient(listOf(fade.copy(alpha = 0f), fade)))
        )
    }
}