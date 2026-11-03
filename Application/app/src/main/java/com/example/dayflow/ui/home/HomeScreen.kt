package com.example.dayflow.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dayflow.navigation.Routes
import com.example.dayflow.ui.challenges.CreateChallengeSheet
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    onCreateChallenge: () -> Unit = {},
    onCreateNote: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var showCreate by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { DayFlowBottomBar(navController) }
    ) { padding ->
        HomeContent(
            state = state,
            modifier = Modifier.padding(padding),
            onToggleHabit = viewModel::toggleHabit,
            onDeleteHabit = viewModel::deleteHabit,
            onCreateHabit = { showAddDialog = true },
            onCreateChallenge = { showCreate = true },
            onCreateNote = onCreateNote,
            onToggleMute = viewModel::toggleMute,
            onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
        )
    }

    if (showAddDialog) {
        AddHabitSheet(
            onDismiss = { showAddDialog = false },
            onCreate = viewModel::addHabit
        )
    }

    if (showCreate) {
        CreateChallengeSheet(
            onDismiss = { showCreate = false },
            onCreate = viewModel::createChallenge
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    modifier: Modifier,
    onToggleHabit: (Long, Boolean) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    onCreateHabit: () -> Unit,
    onCreateChallenge: () -> Unit,
    onCreateNote: () -> Unit,
    onToggleMute: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HomeHeader(state, onToggleMute)
        ProgressCard(state)
        WeekSection(state)
        HabitsSection(state, onToggleHabit, onDeleteHabit)
        QuickActions(onCreateHabit, onCreateChallenge, onCreateNote)
        NotificationHint(
            onClick = onOpenNotifications,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun NotificationHint(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val extra = LocalExtraColors.current
    val shape = RoundedCornerShape(50)

    Row(
        modifier = modifier
            .clip(shape)
            .background(extra.highlight.copy(alpha = 0.08f))
            .border(1.dp, extra.highlight.copy(alpha = 0.30f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            Icons.Outlined.NotificationsActive,
            contentDescription = null,
            tint = extra.highlight,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "برای فعال‌سازی اعلان‌ها اینجا بزن",
            color = extra.highlight,
            style = MaterialTheme.typography.labelMedium
        )
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
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White)
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = greeting,
                color = colors.onSurface,
                style = MaterialTheme.typography.titleMedium
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


            MuteBellButton(muted = state.isMuted, onClick = onNotificationsClick)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MuteBellButton(muted: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val bg by animateColorAsState(
        if (muted) colors.error.copy(alpha = 0.15f) else colors.surfaceVariant,
        tween(300),
        label = "bg"
    )
    val borderColor by animateColorAsState(
        if (muted) colors.error else colors.outline, tween(300), label = "border"
    )
    val tint by animateColorAsState(
        if (muted) colors.error else colors.onSurface, tween(300), label = "tint"
    )

    val scale = remember { Animatable(1f) }
    val swing = remember { Animatable(0f) }
    var firstRun by remember { mutableStateOf(true) }

    LaunchedEffect(muted) {
        if (firstRun) {
            firstRun = false; return@LaunchedEffect
        }
        launch {
            scale.animateTo(0.8f, tween(90))
            scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        }
        if (!muted) {
            repeat(3) {
                swing.animateTo(18f, tween(60))
                swing.animateTo(-18f, tween(60))
            }
        }
        swing.animateTo(0f, tween(60))
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = muted,
            transitionSpec = {
                (scaleIn(tween(220)) + fadeIn(tween(220))) togetherWith
                        (scaleOut(tween(150)) + fadeOut(tween(150)))
            },
            label = "bellIcon"
        ) { isMuted ->
            Icon(
                imageVector = if (isMuted) Icons.Outlined.NotificationsOff else Icons.Outlined.Notifications,
                contentDescription = if (isMuted) "فعال‌سازی اعلان‌ها" else "بی‌صدا کردن اعلان‌ها",
                tint = tint,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        rotationZ = swing.value
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    }
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Caption("پیشرفت امروز")
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
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
            style = MaterialTheme.typography.titleMedium
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
                    style = MaterialTheme.typography.labelMedium
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

        Caption("سبز: انجام شده   خط‌چین: از دست رفته")
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
            style = if (isToday) {
                MaterialTheme.typography.labelMedium
            } else {
                MaterialTheme.typography.bodySmall
            }
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
                    style = MaterialTheme.typography.labelLarge
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
private fun HabitsSection(
    state: HomeUiState,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
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
                    HabitRow(habit, onToggle, onDelete)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitRow(
    habit: HabitUi,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val (icon, tint) = habitVisual(habit.iconKey)
    var confirmDelete by remember { mutableStateOf(false) }

    val progressText = habit.targetDays
        ?.let { "${habit.streak.toPersianDigits()} از ${it.toPersianDigits()} روز" }
        ?: "${habit.streak.toPersianDigits()} روز پیاپی"
    val subtitle =
        listOfNotNull(habit.reminderMinutes?.toTimeText(), progressText).joinToString(" · ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onToggle(habit.id, habit.isDoneToday) },
                onLongClick = { confirmDelete = true }
            )
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
                style = MaterialTheme.typography.titleSmall
            )
            Caption(subtitle)
        }

        CheckCircle(done = habit.isDoneToday)
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = colors.surface,
            title = {
                Text(
                    "حذف عادت",
                    color = colors.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    "«${habit.title}» حذف بشه؟ تاریخچه‌ی قبلیت توی آمار می‌مونه.",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { onDelete(habit.id); confirmDelete = false }) {
                    Text("حذف", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("انصراف", color = colors.onSurfaceVariant)
                }
            }
        )
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
fun habitVisual(key: String): Pair<ImageVector, Color> {
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


// ───────────── Quick actions ─────────────

@Composable
private fun QuickActions(
    onCreateHabit: () -> Unit,
    onCreateChallenge: () -> Unit,
    onCreateNote: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAction(
            "ایجاد عادت",
            Icons.Outlined.Add,
            extra.success,
            onCreateHabit,
            Modifier.weight(1f)
        )
        QuickAction(
            "ایجاد چالش",
            Icons.Outlined.Flag,
            colors.secondary,
            onCreateChallenge,
            Modifier.weight(1f)
        )
        QuickAction(
            "ایجاد یادداشت",
            Icons.Outlined.NoteAlt,
            colors.primary,
            onCreateNote,
            Modifier.weight(1f)
        )

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
                .padding(vertical = 18.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Text(
                text = label,
                color = colors.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
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
            style = MaterialTheme.typography.titleMedium
        )
        trailing()
    }
}

@Composable
private fun Caption(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall
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
        Icon(
            icon,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = text,
            color = colors.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}
