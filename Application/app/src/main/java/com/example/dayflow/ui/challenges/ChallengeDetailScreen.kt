package com.example.dayflow.ui.challenges

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dayflow.data.local.ChallengeStatus
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChallengeDetailScreen(
    onBack: () -> Unit,
    viewModel: ChallengeDetailViewModel = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.uiState.collectAsState()
    var confirmDelete by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopIconButton(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", onBack)
                Text(
                    "جزئیات چالش",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                TopIconButton(Icons.Outlined.Delete, "حذف چالش") { confirmDelete = true }
            }
        }
    ) { padding ->
        val detail = state ?: return@Scaffold
        val c = detail.challenge
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroCard(c)
            StatsRow(c, detail.daysLeft)
            if (detail.dots.isNotEmpty()) DaysCard(detail.dots)
            Actions(c, viewModel)
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = colors.surfaceVariant,
            title = { Text("حذف چالش", style = MaterialTheme.typography.titleMedium) },
            text = { Text("این چالش و همه‌ی پیشرفتش پاک می‌شه. مطمئنی؟", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.delete(onBack)
                }) { Text("حذف", color = colors.error) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("لغو") } }
        )
    }
}

@Composable
private fun HeroCard(c: ChallengeUi) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val (c1, c2) = when (c.status) {
        ChallengeStatus.PAUSED -> extra.highlight to extra.highlight
        ChallengeStatus.COMPLETED -> extra.success to extra.success
        else -> colors.primary to colors.secondary
    }

    FlowSurface {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChallengeIconBox(c.iconKey, 52.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(c.title, style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TypeBadge(c.isGroup)
                        StatusBadge(c.status)
                    }
                }
            }
            ProgressRingBig(c.progress, c1, c2)
            Text(
                "${c.doneDays.toPersianDigits()} از ${c.totalDays.toPersianDigits()} روز انجام شده",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.CalendarToday, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Text(c.dateText, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            if (c.status == ChallengeStatus.ACTIVE && c.streak > 0) StreakLabel(c.streak)
        }
    }
}

@Composable
private fun ProgressRingBig(progress: Float, c1: Color, c2: Color) {
    val colors = MaterialTheme.colorScheme
    val animated by animateFloatAsState(progress, tween(700), label = "detailRing")
    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = colors.outline, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke)
            )
            if (animated > 0f) {
                rotate(-90f) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(c1, c2, c1)),
                        startAngle = 0f, sweepAngle = 360f * animated, useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
            }
        }
        Text(
            "${(animated * 100).roundToInt().toPersianDigits()}٪",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onSurface
        )
    }
}

@Composable
private fun StatsRow(c: ChallengeUi, daysLeft: Int) {
    val extra = LocalExtraColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard("پیاپی", "${c.streak.toPersianDigits()} روز", extra.highlight, Icons.Outlined.LocalFireDepartment, Modifier.weight(1f))
        StatCard("انجام‌شده", "${c.doneDays.toPersianDigits()} روز", extra.success, Icons.Outlined.Check, Modifier.weight(1f))
        StatCard("مانده", "${daysLeft.toPersianDigits()} روز", MaterialTheme.colorScheme.secondary, Icons.Outlined.Schedule, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, tint: Color, icon: ImageVector, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, shape)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
        Text(label, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
    }
}

@Composable
private fun DaysCard(dots: List<DotStatus>) {
    FlowSurface {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("روزهای چالش", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            dots.chunked(10).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { DayDot(it) }
                }
            }
        }
    }
}

@Composable
private fun DayDot(status: DotStatus) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val base = Modifier.size(20.dp)
    when (status) {
        DotStatus.Done -> Box(base.background(extra.success, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Check, null, tint = colors.background, modifier = Modifier.size(12.dp))
        }
        DotStatus.Today -> Box(base.background(extra.brandGradient, CircleShape))
        DotStatus.Missed -> Box(base.background(colors.onSurfaceVariant.copy(alpha = 0.2f), CircleShape))
        DotStatus.Future -> Box(base.border(1.dp, colors.outline, CircleShape))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Actions(c: ChallengeUi, vm: ChallengeDetailViewModel) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (c.status) {
            ChallengeStatus.ACTIVE ->
                if (c.doneToday) {
                    BigButton("امروز انجام شد · برای لغو بزن", Icons.Outlined.Check, extra.success, filled = false) {
                        vm.toggleToday(true)
                    }
                } else {
                    BigButton("امروز انجام دادم", Icons.Outlined.Check, null, filled = true) {
                        vm.toggleToday(false)
                    }
                }
            ChallengeStatus.UPCOMING ->
                BigButton("فعال‌سازی چالش", Icons.Outlined.PlayArrow, null, filled = true) { vm.activate() }
            ChallengeStatus.PAUSED ->
                InfoBox("چالش متوقف شده. با دکمه‌ی پایین ادامه‌اش بده؛ روزهای توقف به پایان چالش اضافه می‌شن.", extra.highlight)
            ChallengeStatus.COMPLETED ->
                InfoBox("این چالش تموم شد. آفرین!", extra.success)
        }

        when (c.status) {
            ChallengeStatus.ACTIVE ->
                BigButton("توقف چالش", Icons.Outlined.Pause, extra.highlight, filled = false) { vm.pause() }
            ChallengeStatus.PAUSED ->
                BigButton("ادامه‌ی چالش", Icons.Outlined.PlayArrow, extra.success, filled = false) { vm.resume() }
            else -> Unit
        }
    }
}

/** tint = null یعنی دکمه‌ی گرادیانی برند */
@Composable
private fun BigButton(label: String, icon: ImageVector, tint: Color?, filled: Boolean, onClick: () -> Unit) {
    val extra = LocalExtraColors.current
    val shape = RoundedCornerShape(16.dp)
    val contentColor = if (filled) Color.White else tint ?: Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .then(
                if (filled) Modifier.background(extra.brandGradient)
                else Modifier
                    .background((tint ?: Color.White).copy(alpha = 0.12f))
                    .border(1.dp, tint ?: Color.White, shape)
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

@Composable
private fun InfoBox(text: String, tint: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(tint.copy(alpha = 0.12f))
            .padding(16.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = tint, textAlign = TextAlign.Start)
    }
}