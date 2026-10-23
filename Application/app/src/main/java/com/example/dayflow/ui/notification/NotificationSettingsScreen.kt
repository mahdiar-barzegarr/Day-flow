package com.example.dayflow.ui.notification

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private class NPalette(
    val bg: Color, val card: Color, val outline: Color,
    val onSurface: Color, val variant: Color,
    val primary: Color, val secondary: Color, val highlight: Color
)

private val DarkPalette = NPalette(
    bg = Color(0xFF080D18), card = Color(0xFF151F32), outline = Color(0xFF1F2C45),
    onSurface = Color(0xFFF4F6FC), variant = Color(0xFF9AA7BC),
    primary = Color(0xFF7865FF), secondary = Color(0xFF438BFF), highlight = Color(0xFFFFB454)
)

private val LightPalette = NPalette(
    bg = Color(0xFFF5F7FC), card = Color(0xFFFFFFFF), outline = Color(0xFFE3E8F3),
    onSurface = Color(0xFF101827), variant = Color(0xFF5E6B80),
    primary = Color(0xFF7865FF), secondary = Color(0xFF438BFF), highlight = Color(0xFFE8942A)
)

@Composable
fun NotificationSettingsScreen(
    onBack: (() -> Unit)? = null,
    vm: NotificationSettingsViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val c = remember(isDark) { if (isDark) DarkPalette else LightPalette }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    fun ensurePermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= 31) {
            val am = ctx.getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                ctx.startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:${ctx.packageName}")
                    )
                )
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.bg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = c.onSurface)
                }
                Spacer(Modifier.width(4.dp))
            }
            Column {
                Text(
                    "مدیریت اعلان‌ها",
                    style = MaterialTheme.typography.headlineSmall,
                    color = c.onSurface
                )
                Text(
                    "انتخاب کن عادت‌هات چطور یادآوری بشن",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.variant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OptionCard(
            c = c,
            icon = Icons.Rounded.Alarm,
            accent = c.highlight,
            title = "الارم",
            description = "سر ساعت هر عادت، گوشی با صدای الارم و عنوان عادت زنگ می‌زنه.",
            checked = vm.alarmOn,
            onChange = {
                if (it) ensurePermissions()
                vm.setAlarm(it)
            }
        )

        OptionCard(
            c = c,
            icon = Icons.Rounded.Notifications,
            accent = c.secondary,
            title = "نوتیفیکیشن",
            description = "سر ساعت هر عادت فقط یک نوتیف ساده برات میاد.",
            checked = vm.notifOn,
            onChange = {
                if (it) ensurePermissions()
                vm.setNotif(it)
            }
        )

        // Info card
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(c.primary.copy(alpha = 0.10f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Schedule, null, tint = c.primary)
            Text(
                "اگه تا ساعت ۲۱:۰۰ عادت یا چالش انجام‌نشده داشته باشی، هر روز یه پیام یادآوری برات میاد.",
                style = MaterialTheme.typography.bodySmall,
                color = c.onSurface
            )
        }
    }
}

@Composable
private fun OptionCard(
    c: NPalette,
    icon: ImageVector,
    accent: Color,
    title: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = c.card,
        border = BorderStroke(1.dp, if (checked) accent.copy(alpha = 0.6f) else c.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent)
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = c.onSurface)
                Text(description, style = MaterialTheme.typography.bodySmall, color = c.variant)
            }

            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = c.primary,
                    uncheckedThumbColor = c.variant,
                    uncheckedTrackColor = c.outline,
                    uncheckedBorderColor = c.outline
                )
            )
        }
    }
}