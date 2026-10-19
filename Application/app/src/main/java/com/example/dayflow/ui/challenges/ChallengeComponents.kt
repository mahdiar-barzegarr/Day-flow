package com.example.dayflow.ui.challenges

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dayflow.data.local.ChallengeStatus
import com.example.dayflow.ui.home.habitVisual
import com.example.dayflow.ui.theme.LocalExtraColors

@Composable
internal fun FlowSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    val base = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(colors.surfaceVariant)
        .border(1.dp, colors.outline, shape)
    Column(modifier = if (onClick != null) base.clickable(onClick = onClick) else base, content = content)
}

@Composable
internal fun TopIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(shape)
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = colors.onSurface, modifier = Modifier.size(22.dp))
    }
}

@Composable
internal fun Pill(text: String, color: Color, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
internal fun TypeBadge(isGroup: Boolean) {
    val colors = MaterialTheme.colorScheme
    if (isGroup) Pill("گروهی", colors.primary, Icons.Outlined.Groups)
    else Pill("شخصی", colors.onSurfaceVariant, Icons.Outlined.Person)
}

@Composable
internal fun StatusBadge(status: ChallengeStatus) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    when (status) {
        ChallengeStatus.UPCOMING -> Pill("برنامه‌ریزی‌شده", colors.secondary)
        ChallengeStatus.PAUSED -> Pill("متوقف‌شده", extra.highlight, Icons.Outlined.Pause)
        ChallengeStatus.COMPLETED -> Pill("تکمیل‌شده", extra.success, Icons.Outlined.Check)
        ChallengeStatus.ACTIVE -> Unit
    }
}

@Composable
internal fun StreakLabel(streak: Int) {
    val color = LocalExtraColors.current.highlight
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Outlined.LocalFireDepartment, null, tint = color, modifier = Modifier.size(16.dp))
        Text(streakText(streak), style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
internal fun ChallengeIconBox(iconKey: String, size: Dp = 44.dp) {
    val (icon, tint) = habitVisual(iconKey)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size / 2))
    }
}

@Composable
internal fun ChallengeProgressBar(progress: Float, brush: Brush) {
    val colors = MaterialTheme.colorScheme
    val animated by animateFloatAsState(progress, tween(600), label = "challengeBar")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(colors.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(50))
                .background(brush)
        )
    }
}

@Composable
internal fun LabeledSection(label: String, hint: String? = null, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = colors.onSurface)
            if (hint != null) Text(hint, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
        content()
    }
}

@Composable
internal fun challengeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary
)