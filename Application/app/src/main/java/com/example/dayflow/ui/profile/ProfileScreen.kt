package com.example.dayflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.dayflow.navigation.Routes
import com.example.dayflow.ui.challenges.ChallengeProgressBar
import com.example.dayflow.ui.challenges.FlowSurface
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlin.collections.forEachIndexed
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController,
    state: ProfileUiState,
    onEditProfile: () -> Unit = {},
    onSeeAllChallenges: () -> Unit = {},
    onChallengeClick: (Long) -> Unit = {},
    onSeeAllNotes: () -> Unit = {},
    onNoteClick: (Long) -> Unit = {},
    onThemeClick: () -> Unit = {},
    onBackupClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Scaffold(
        containerColor = colors.background,
        bottomBar = { DayFlowBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .statusBarsPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("پروفایل", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)

            ProfileHeader(state, onSaveName = viewModel::saveName)
            StatsRow(state)
            RecentNotes(state, onSeeAllNotes, onNoteClick)
            ActiveChallenges(state, onSeeAllChallenges, onChallengeClick)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader("بیشتر")
                FlowSurface {
                    SettingRow(
                        icon = Icons.Outlined.Notifications,
                        title = "مدیریت یادآورها",
                        value = null,
                        tint = extra.highlight,
                        showChevron = true,
                        onClick = { navController.navigate(Routes.NOTIFICATIONS) }
                    )
                    Divider()
                    SettingRow(
                        icon = Icons.Outlined.Info,
                        title = "درباره دی‌فلو",
                        value = null,
                        tint = colors.secondary,
                        showChevron = true,
                        onClick = { navController.navigate(Routes.ABOUT) }
                    )
                }

            }
        }
    }
}





@Composable
private fun RecentNotes(
    state: ProfileUiState,
    onSeeAll: () -> Unit,
    onNoteClick: (Long) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("آخرین نت‌ها") {
            Text("همه", style = MaterialTheme.typography.labelMedium,
                color = colors.secondary, modifier = Modifier.clickable(onClick = onSeeAll))
        }
        FlowSurface {
            if (state.recentNotes.isEmpty()) {
                Text("هنوز نتی ننوشتی.", style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant, modifier = Modifier.padding(20.dp))
            } else {
                Column {
                    state.recentNotes.forEachIndexed { i, n ->
                        if (i > 0) Divider()
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onNoteClick(n.id) }
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(n.title, style = MaterialTheme.typography.titleSmall,
                                color = colors.onSurface, maxLines = 1)
                            if (n.preview.isNotBlank()) {
                                Text(n.preview, style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(state: ProfileUiState) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatTile(state.streakDays.toPersianDigits(), "روز پیاپی", extra.highlight, Modifier.weight(1f).fillMaxHeight())
        StatTile(state.totalHabits.toPersianDigits(), "عادت", colors.onSurface, Modifier.weight(1f).fillMaxHeight())
        StatTile(state.activeChallengesCount.toPersianDigits(), "چالش فعال", extra.success, Modifier.weight(1f).fillMaxHeight())
        StatTile(state.totalNotes.toPersianDigits(), "نت", colors.secondary, Modifier.weight(1f).fillMaxHeight())
    }
}


@Composable
private fun ProfileHeader(state: ProfileUiState, onSaveName: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    var editing by remember(state.name) { mutableStateOf(state.name.isBlank()) }
    var input by remember(state.name) { mutableStateOf(state.name) }
    val initial = state.name.trim().firstOrNull()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(CircleShape).background(extra.brandGradient),
            contentAlignment = Alignment.Center
        ) {
            if (initial != null) {
                Text(initial.toString(), style = MaterialTheme.typography.displayMedium, color = Color.White)
            } else {
                Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(44.dp))
            }
        }

        if (editing) {
            FlowSurface {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("اسمت چیه؟", style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                    OutlinedTextField(
                        value = input,
                        onValueChange = { if (it.length <= 20) input = it },
                        singleLine = true,
                        placeholder = { Text("مثلاً: سارا") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(extra.brandGradient)
                            .clickable(enabled = input.isNotBlank()) {
                                onSaveName(input)
                                editing = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ذخیره", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable { editing = true }
            ) {
                Text(state.name, style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
                Icon(Icons.Outlined.Edit, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            if (state.bio.isNotBlank()) {
                Text(state.bio, style = MaterialTheme.typography.bodyMedium,
                    color = colors.secondary, textAlign = TextAlign.Center)
            }
        }
    }
}



@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier) {
    FlowSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                maxLines = 1,
                softWrap = false
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActiveChallenges(
    state: ProfileUiState,
    onSeeAll: () -> Unit,
    onChallengeClick: (Long) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("چالش‌های فعال") {
            Text(
                "همه",
                style = MaterialTheme.typography.labelMedium,
                color = colors.secondary,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
        FlowSurface {
            if (state.activeChallenges.isEmpty()) {
                Text(
                    "هنوز چالش فعالی نداری.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.activeChallenges.forEach { c ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChallengeClick(c.id) },
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(c.title, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                                Text(
                                    "${(c.progress * 100).roundToInt().toPersianDigits()}٪",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            ChallengeProgressBar(c.progress, extra.brandGradient)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String?,
    tint: Color,
    showChevron: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = if (tint == colors.error) colors.error else colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight, null,
                tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Composable
private fun SectionHeader(title: String, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}