package com.example.dayflow.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    onCreateChallenge: () -> Unit = {},
    onCreateGroup: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { DayFlowBottomBar(navController) }
    ) { padding ->
        HomeContent(
            state = state,
            modifier = Modifier.padding(padding),
            onToggleHabit = viewModel::toggleHabit,
            onCreateHabit = { showAddDialog = true },
            onCreateChallenge = onCreateChallenge,
            onCreateGroup = onCreateGroup,
            onNotificationsClick = onNotificationsClick
        )
    }

    // موقت: تا وقتی صفحه‌ی ساخت عادت ساخته نشده
    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.addHabit(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    modifier: Modifier,
    onToggleHabit: (Long, Boolean) -> Unit,
    onCreateHabit: () -> Unit,
    onCreateChallenge: () -> Unit,
    onCreateGroup: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HomeHeader(state, onNotificationsClick)
        ProgressCard(state)
        WeekSection(state)
        HabitsSection(state, onToggleHabit)
        GroupSection()
        QuickActions(onCreateHabit, onCreateChallenge, onCreateGroup)
    }
}

// ───────────── Header ─────────────

@Composable
private fun HomeHeader(state: HomeUiState, onNotificationsClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val greeting = if (state.userName.isBlank()) {
        "سلام، آماده‌ای امروز پیشرفت کنی؟"
    } else {
        "سلام ${state.userName}، آماده‌ای امروز پیشرفت کنی؟"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(extra.brandGradient),
            contentAlignment = Alignment.Center
        ) {
            val initial = state.userName.trim().firstOrNull()
            if (initial != null) {
                Text(
                    text = initial.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White)
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = greeting,
                color = colors.onSurface,
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Caption(state.dateText)
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceVariant)
                .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
                .clickable(onClick = onNotificationsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "اعلان‌ها",
                tint = colors.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ───────────── Progress ─────────────

@Composable
private fun ProgressCard(state: HomeUiState) {
    val remaining = state.totalCount - state.doneCount
    val title = if (state.totalCount == 0) {
        "هنوز عادتی نداری"
    } else {
        "${state.doneCount.toPersianDigits()} از ${state.totalCount.toPersianDigits()} عادت انجام شد"
    }
    val message = when {
        state.totalCount == 0 -> "اولین عادتت رو بساز و شروع کن."
        remaining == 0 -> "آفرین! امروزت کامل شد."
        remaining == 1 -> "فقط یک قدم مونده تا امروزت کامل بشه."
        else -> "${remaining.toPersianDigits()} عادت دیگه مونده."
    }

    FlowCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Caption("پیشرفت امروز")
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Caption(message)
            }
            ProgressRing(progress = state.progress)
        }
    }
}

@Composable
private fun ProgressRing(progress: Float) {
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progressRing"
    )
    val colors = MaterialTheme.colorScheme

    Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 9.dp.toPx()
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = colors.outline,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeWidth)
            )
            if (animated > 0f) {
                rotate(-90f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(colors.primary, colors.secondary, colors.primary)
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f * animated,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
        }
        Text(
            text = "${(animated * 100).roundToInt().toPersianDigits()}٪",
            color = colors.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ───────────── Week ─────────────

@Composable
private fun WeekSection(state: HomeUiState) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val streakColor = if (state.streakDays > 0) extra.highlight else colors.onSurfaceVariant

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("این هفته") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = streakColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${state.streakDays.toPersianDigits()} روز پیاپی",
                    color = streakColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        FlowCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                state.week.forEach { WeekDayItem(it) }
            }
        }

        Caption("سبز: انجام شده   خط‌چین: از دست رفته", fontSize = 11.sp)
    }
}

@Composable
private fun WeekDayItem(day: WeekDayUi) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val isToday = day.status == DayStatus.Today

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = day.label,
            color = if (isToday) colors.onSurface else colors.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )

        when (day.status) {
            DayStatus.Done -> Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(extra.success.copy(alpha = 0.15f))
                    .border(1.5.dp, extra.success, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = extra.success,
                    modifier = Modifier.size(18.dp)
                )
            }

            DayStatus.Missed -> {
                val dashColor = colors.onSurfaceVariant.copy(alpha = 0.7f)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .drawBehind {
                            drawCircle(
                                color = dashColor,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(1.5.dp)
                            .background(dashColor)
                    )
                }
            }

            DayStatus.Today -> Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(extra.brandGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayNumber.toPersianDigits(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            DayStatus.None -> Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, colors.outline, CircleShape)
            )
        }
    }
}

// ───────────── Habits ─────────────

@Composable
private fun HabitsSection(state: HomeUiState, onToggle: (Long, Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("عادت‌های امروز") {
            if (state.totalCount > 0) {
                Caption("${state.doneCount.toPersianDigits()} از ${state.totalCount.toPersianDigits()}")
            }
        }

        if (state.habits.isEmpty()) {
            FlowCard {
                EmptyMessage(
                    icon = Icons.Outlined.EventAvailable,
                    text = "با دکمه‌ی «ایجاد عادت» اولین عادتت رو اضافه کن."
                )
            }
        } else {
            FlowCard {
                state.habits.forEachIndexed { index, habit ->
                    HabitRow(habit, onToggle)
                    if (index < state.habits.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colors.outline)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitRow(habit: HabitUi, onToggle: (Long, Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val (icon, tint) = habitVisual(habit.iconKey)
    val subtitle = listOfNotNull(
        habit.goalText,
        "${habit.streak.toPersianDigits()} روز پیاپی"
    ).joinToString(" · ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(habit.id, habit.isDoneToday) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = habit.title,
                color = colors.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Caption(subtitle)
        }

        CheckCircle(done = habit.isDoneToday)
    }
}

@Composable
private fun CheckCircle(done: Boolean) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    if (done) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(extra.success),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "انجام شد",
                tint = colors.background,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(1.5.dp, colors.onSurfaceVariant.copy(alpha = 0.5f), CircleShape)
        )
    }
}

@Composable
private fun habitVisual(key: String): Pair<ImageVector, Color> {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    return when (key) {
        "water" -> Icons.Outlined.WaterDrop to colors.secondary
        "book" -> Icons.Outlined.AutoStories to extra.highlight
        "fitness" -> Icons.Outlined.FitnessCenter to colors.primary
        "sleep" -> Icons.Outlined.Bedtime to colors.primary
        else -> Icons.Outlined.Star to extra.success
    }
}

// ───────────── Group ─────────────

@Composable
private fun GroupSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("فعالیت بعدی گروه")
        FlowCard {
            EmptyMessage(
                icon = Icons.Outlined.Groups,
                text = "هنوز عضو گروهی نیستی. یه گروه بساز و با دوستات با هم پیشرفت کنین."
            )
        }
    }
}

// ───────────── Quick actions ─────────────

@Composable
private fun QuickActions(
    onCreateHabit: () -> Unit,
    onCreateChallenge: () -> Unit,
    onCreateGroup: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAction("ایجاد عادت", Icons.Outlined.Add, extra.success, onCreateHabit, Modifier.weight(1f))
        QuickAction("ایجاد چالش", Icons.Outlined.Flag, colors.secondary, onCreateChallenge, Modifier.weight(1f))
        QuickAction("ساخت گروه", Icons.Outlined.Groups, colors.primary, onCreateGroup, Modifier.weight(1f))
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Text(
                text = label,
                color = colors.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ───────────── Add habit (موقت) ─────────────

@Composable
private fun AddHabitDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceVariant,
        title = {
            Text("ایجاد عادت", color = colors.onSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("عنوان عادت") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            TextButton(enabled = title.isNotBlank(), onClick = { onConfirm(title) }) {
                Text("ثبت")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لغو") }
        }
    )
}

// ───────────── Shared bits ─────────────

@Composable
private fun FlowCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.outline)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SectionHeader(title: String, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        trailing()
    }
}

@Composable
private fun Caption(text: String, fontSize: androidx.compose.ui.unit.TextUnit = 12.sp) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = fontSize,
        lineHeight = fontSize * 1.5f
    )
}

@Composable
private fun EmptyMessage(icon: ImageVector, text: String) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(28.dp))
        Text(
            text = text,
            color = colors.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}
