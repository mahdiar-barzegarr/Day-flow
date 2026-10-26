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
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController,
    state: ProfileUiState,
    onLogin: () -> Unit = { navController.navigate("login") },
    onRegister: () -> Unit = { navController.navigate("register") },
    onAboutClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onSeeAllChallenges: () -> Unit = {},
    onChallengeClick: (Long) -> Unit = {},
    onSettingClick: (ProfileSetting) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

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

            if (state.isLoggedIn) {
                ProfileHeader(state, onEditProfile)
                StatsRow(state)
                ActiveChallenges(state, onSeeAllChallenges, onChallengeClick)
                SettingsSection(
                    onRemindersClick = { navController.navigate(Routes.NOTIFICATIONS) },
                    onLogout = { showLogoutDialog = true }
                )
                Text(
                    "اطلاعات شخصی مثل ایمیل به‌طور پیش‌فرض برای دیگران نمایش داده نمی‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                GuestContent(
                    state = state,
                    onLogin = onLogin,
                    onRegister = onRegister,
                    onThemeClick = { onSettingClick(ProfileSetting.Theme) },
                    onAboutClick = onAboutClick,
                    onNtifClick = {navController.navigate(Routes.NOTIFICATIONS)}
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colors.surface,
            titleContentColor = colors.onSurface,
            textContentColor = colors.onSurfaceVariant,
            shape = RoundedCornerShape(20.dp),
            title = { Text("خروج از حساب") },
            text = { Text("آیا مطمئنی که می‌خواهی از حساب خارج شوی؟") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                }) {
                    Text("بله، خروج", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("انصراف", color = colors.onSurfaceVariant)
                }
            }
        )
    }

    if (isLoggingOut) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.secondary)
            }
        }
    }
}

// ───────────────────────── حالت مهمان ─────────────────────────

@Composable
private fun GuestContent(
    state: ProfileUiState,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onThemeClick: () -> Unit,
    onAboutClick: () -> Unit,
    onNtifClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val buttonShape = RoundedCornerShape(20.dp)
    val dashColor = colors.onSurfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .drawBehind {
                    drawCircle(
                        color = dashColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Person, null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "وارد حساب خودت شو",
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface
        )
        Text(
            "با ورود، عادت‌ها، چالش‌ها و گروه‌هایت در همه دستگاه‌ها همگام می‌شوند.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    FlowSurface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BenefitRow(Icons.Outlined.Sync, "پیشرفت و استریک‌هایت هیچ‌وقت گم نمی‌شود", extra.success)
            BenefitRow(Icons.Outlined.Groups, "گروه بساز و با دوستانت برنامه هماهنگ کن", colors.secondary)
            BenefitRow(Icons.Outlined.Notifications, "یادآور و دعوت‌ها را به‌موقع دریافت کن", extra.highlight)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(buttonShape)
                .background(extra.brandGradient)
                .clickable(onClick = onLogin),
            contentAlignment = Alignment.Center
        ) {
            Text("ورود", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(buttonShape)
                .background(colors.surface)
                .border(1.dp, colors.outline, buttonShape)
                .clickable(onClick = onRegister),
            contentAlignment = Alignment.Center
        ) {
            Text("ساخت حساب", style = MaterialTheme.typography.labelLarge, color = colors.onSurface, fontWeight = FontWeight.Bold)
        }
    }

    FlowSurface {
        SettingRow(
            icon = Icons.Outlined.DarkMode,
            title = "ظاهر برنامه",
            value = state.themeLabel,
            tint = colors.onSurface,
            showChevron = true,
            onClick = onThemeClick
        )
        Divider()
        SettingRow(
            icon = Icons.Outlined.Info,
            title = "درباره StreakUp",
            value = null,
            tint = colors.onSurface,
            showChevron = true,
            onClick = onAboutClick
        )
        Divider()
        SettingRow(
            icon = Icons.Outlined.Info,
            title = "مدیریت یاداوری",
            value = null,
            tint = colors.onSurface,
            showChevron = true,
            onClick = onNtifClick
        )
    }
}

@Composable
private fun BenefitRow(icon: ImageVector, text: String, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// ───────────────────────── حالت لاگین‌شده ─────────────────────────

@Composable
private fun ProfileHeader(state: ProfileUiState, onEdit: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val initial = state.name.trim().firstOrNull()
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(extra.brandGradient),
            contentAlignment = Alignment.Center
        ) {
            if (initial != null) {
                Text(initial.toString(), style = MaterialTheme.typography.displayMedium, color = Color.White)
            } else {
                Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(44.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            state.name.ifBlank { "کاربر دی‌فلو" },
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface
        )
        if (state.username.isNotBlank()) {
            Text("@${state.username}", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
        if (state.bio.isNotBlank()) {
            Text(
                state.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.secondary,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(6.dp))
//        Row(
//            modifier = Modifier
//                .clip(shape)
//                .border(1.dp, colors.outline, shape)
//                .clickable(onClick = onEdit)
//                .padding(horizontal = 24.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Text("ویرایش پروفایل", style = MaterialTheme.typography.labelLarge, color = colors.onSurface)
//            Icon(Icons.Outlined.Edit, null, tint = colors.onSurface, modifier = Modifier.size(16.dp))
//        }
    }
}

@Composable
private fun StatsRow(state: ProfileUiState) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatTile(state.streakDays.toPersianDigits(), "روز پیاپی", extra.highlight, Modifier.weight(1f))
        StatTile(state.totalHabitsDone.toPersianDigits(), "عادت انجام‌شده", colors.onSurface, Modifier.weight(1f))
        StatTile(state.completedChallenges.toPersianDigits(), "چالش تمام‌شده", extra.success, Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier) {
    FlowSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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

private data class SettingItem(
    val setting: ProfileSetting,
    val title: String,
    val icon: ImageVector,
    val value: String? = null
)

@Composable
private fun SettingsSection(
    onRemindersClick: () -> Unit,
    onLogout: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("تنظیمات")
        FlowSurface {
            SettingRow(
                icon = Icons.Outlined.Notifications,
                title = "مدیریت یادآورها",
                value = null,
                tint = colors.onSurface,
                showChevron = true,
                onClick = onRemindersClick
            )
            Divider()
            SettingRow(
                icon = Icons.AutoMirrored.Outlined.Logout,
                title = "خروج از حساب",
                value = null,
                tint = colors.error,
                showChevron = false,
                onClick = onLogout
            )
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