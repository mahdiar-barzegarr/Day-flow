package com.example.dayflow.ui.home


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.dayflow.ui.components.WheelTimePickerDialog
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlinx.coroutines.launch

private val habitIconKeys = listOf("star", "water", "book", "fitness", "sleep")
private val durationPresets = listOf(7, 21, 30, 66)
private val timePresets = listOf("صبح" to 7 * 60, "عصر" to 18 * 60, "شب" to 21 * 60)

internal fun Int.toTimeText(): String =
    "${(this / 60).toString().padStart(2, '0')}:${(this % 60).toString().padStart(2, '0')}"
        .map { if (it in '0'..'9') '۰' + (it - '0') else it }
        .joinToString("")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onCreate: (title: String, iconKey: String, targetDays: Int?, reminderMinutes: Int?) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var iconKey by remember { mutableStateOf("star") }
    var targetDays by remember { mutableStateOf<Int?>(null) }
    var minutes by remember { mutableStateOf<Int?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

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
            Text("عادت جدید", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)

            HabitPreview(title, iconKey, targetDays, minutes)

            Section("عنوان", optional = false) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 40) title = it },
                    placeholder = { Text("مثلاً: تمرین باشگاه", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        cursorColor = colors.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // آیکن
            Section("آیکن", optional = true) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    habitIconKeys.forEach { key ->
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
                            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // چند روز (اختیاری)
            Section("چند روز تکرار بشه؟", optional = true) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationPresets.forEach { d ->
                            SheetChip("${d.toPersianDigits()} روز", targetDays == d) {
                                targetDays = if (targetDays == d) null else d
                            }
                        }
                    }
                    targetDays?.let { days ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            StepButton(Icons.Outlined.Remove) { targetDays = (days - 1).coerceAtLeast(1) }
                            Text(
                                "${days.toPersianDigits()} روز",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onSurface
                            )
                            StepButton(Icons.Outlined.Add) { targetDays = (days + 1).coerceAtMost(365) }
                        }
                    }
                }
            }

            // ساعت (اختیاری)
            Section("ساعت انجام", optional = true) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timePresets.forEach { (label, m) ->
                        SheetChip("$label ${m.toTimeText()}", minutes == m) {
                            minutes = if (minutes == m) null else m
                        }
                    }
                    val custom = minutes?.takeIf { m -> timePresets.none { it.second == m } }
                    SheetChip(custom?.toTimeText() ?: "ساعت دلخواه", custom != null) {
                        showTimePicker = true
                    }
                }
            }

            // دکمه‌ی ایجاد
            val enabled = title.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(if (enabled) 1f else 0.35f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(extra.brandGradient)
                    .clickable(enabled = enabled) {
                        onCreate(title.trim(), iconKey, targetDays, minutes)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("ایجاد عادت", style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
        }
    }

    if (showTimePicker) {
        WheelTimePickerDialog(
            initialMinutes = minutes,
            onDismiss = { showTimePicker = false },
            onConfirm = {
                minutes = it
                showTimePicker = false
            }
        )
    }
}

@Composable
private fun HabitPreview(title: String, iconKey: String, targetDays: Int?, minutes: Int?) {
    val colors = MaterialTheme.colorScheme
    val (icon, tint) = habitVisual(iconKey)
    val subtitle = listOfNotNull(targetDays?.let { "${it.toPersianDigits()} روز" }, minutes?.toTimeText())
        .joinToString(" · ")
        .ifEmpty { "هر روز" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.ifBlank { "اسم عادتت اینجا دیده می‌شه" },
                style = MaterialTheme.typography.titleSmall,
                color = if (title.isBlank()) colors.onSurfaceVariant else colors.onSurface
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
        Box(modifier = Modifier.size(26.dp).border(1.5.dp, colors.onSurfaceVariant.copy(alpha = 0.5f), CircleShape))
    }
}

@Composable
private fun Section(label: String, optional: Boolean, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = colors.onSurface)
            Text(
                if (optional) "اختیاری" else "ضروری",
                style = MaterialTheme.typography.bodySmall,
                color = if (optional) colors.onSurfaceVariant else colors.error
            )
        }
        content()
    }
}

@Composable
internal fun SheetChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) colors.primary.copy(alpha = 0.22f) else colors.surfaceVariant)
            .border(1.dp, if (selected) colors.primary else colors.outline, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.onSurface else colors.onSurfaceVariant
        )
    }
}

@Composable
internal fun StepButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(18.dp)) }
}