package com.example.dayflow.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.dayflow.ui.challenges.ChallengeProgressBar
import com.example.dayflow.ui.challenges.FlowSurface
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlin.math.roundToInt

private val AvatarColors = listOf(
    Color(0xFF2DD4A0), Color(0xFF3B82F6), Color(0xFFFFB84D), Color(0xFF8B7CFF)
)
private val OnAvatar = Color(0xFF0B1220)

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupsViewModel = hiltViewModel(),
    onOpenGroup: (Long) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    var showCreate by remember { mutableStateOf(false) }
    var showJoin by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = colors.background,
        bottomBar = { DayFlowBottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (state.isLoggedIn) {
            LoggedInGroups(
                state = state,
                modifier = Modifier.padding(padding),
                onQueryChanged = viewModel::onQueryChanged,
                onCreateClick = { showCreate = true },
                onJoinClick = { showJoin = true },
                onAccept = viewModel::acceptInvite,
                onReject = viewModel::rejectInvite,
                onOpenGroup = onOpenGroup
            )
        } else {
            GuestGroups(
                modifier = Modifier.padding(padding),
                onLogin = { navController.navigate("login") },
                onRegister = { navController.navigate("register") }
            )
        }
    }

    if (showCreate) {
        CreateGroupDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, privacy ->
                viewModel.createGroup(name, privacy)
                showCreate = false
            }
        )
    }
    if (showJoin) {
        JoinGroupDialog(
            onDismiss = { showJoin = false },
            onJoin = { code ->
                viewModel.joinByCode(code)
                showJoin = false
            }
        )
    }
}

// ───────────────────────── لاگین‌شده ─────────────────────────

@Composable
private fun LoggedInGroups(
    state: GroupsUiState,
    modifier: Modifier,
    onQueryChanged: (String) -> Unit,
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onOpenGroup: (Long) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { GroupsHeader(onCreateClick, onJoinClick) }

        item {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChanged,
                placeholder = { Text("جستجو در گروه‌های من", color = colors.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = colors.onSurfaceVariant) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedBorderColor = colors.secondary,
                    unfocusedBorderColor = colors.outline,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.invites, key = { "invite_${it.id}" }) { invite ->
            InviteCard(invite, onAccept = { onAccept(invite.id) }, onReject = { onReject(invite.id) })
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("گروه‌های من", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                Text(
                    "${state.totalGroups.toPersianDigits()} گروه",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }

        if (state.groups.isEmpty()) {
            item {
                FlowSurface {
                    Text(
                        text = if (state.query.isBlank()) "هنوز عضو هیچ گروهی نیستی. یکی بساز یا با کد دعوت بپیوند."
                        else "گروهی با این نام پیدا نشد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }
        } else {
            items(state.groups, key = { it.id }) { group ->
                GroupCard(group, onOpen = { onOpenGroup(group.id) })
            }
        }
    }
}

@Composable
private fun GroupsHeader(onCreateClick: () -> Unit, onJoinClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "گروه‌ها",
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(shape)
                .background(colors.surface)
                .border(1.dp, colors.outline, shape)
                .clickable(onClick = onJoinClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Link, "پیوستن با کد", tint = colors.onSurface, modifier = Modifier.size(20.dp))
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(shape)
                .background(extra.brandGradient)
                .clickable(onClick = onCreateClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Add, "ساخت گروه", tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun InviteCard(invite: InviteUi, onAccept: () -> Unit, onReject: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val shape = RoundedCornerShape(18.dp)
    val btnShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, extra.highlight.copy(alpha = 0.6f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Avatar(invite.inviterName.trim().firstOrNull()?.toString() ?: "؟", 0, 32)
            Text(
                "${invite.inviterName} تو را به گروه «${invite.groupName}» دعوت کرده است.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(btnShape)
                    .background(extra.success)
                    .clickable(onClick = onAccept),
                contentAlignment = Alignment.Center
            ) {
                Text("پذیرفتن", color = OnAvatar, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(btnShape)
                    .background(colors.background)
                    .border(1.dp, colors.outline, btnShape)
                    .clickable(onClick = onReject),
                contentAlignment = Alignment.Center
            ) {
                Text("رد کردن", color = colors.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GroupCard(group: GroupUi, onOpen: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    val tileColor = when (group.icon) {
        GroupIcon.GYM -> colors.secondary
        GroupIcon.BOOK -> extra.highlight
        GroupIcon.FIRE -> extra.success
        GroupIcon.HEART -> colors.error
        GroupIcon.GROUP -> colors.secondary
    }
    val tileIcon: ImageVector = when (group.icon) {
        GroupIcon.GYM -> Icons.Outlined.FitnessCenter
        GroupIcon.BOOK -> Icons.Outlined.MenuBook
        GroupIcon.FIRE -> Icons.Outlined.LocalFireDepartment
        GroupIcon.HEART -> Icons.Outlined.FavoriteBorder
        GroupIcon.GROUP -> Icons.Outlined.Groups
    }
    val privacyIcon = if (group.privacy == GroupPrivacy.PRIVATE) Icons.Outlined.Lock else Icons.Outlined.Link
    val privacyText = if (group.privacy == GroupPrivacy.PRIVATE) "خصوصی" else "با کد دعوت"

    FlowSurface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // بالا: آیکون + نام + آواتارها
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(tileColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tileIcon, null, tint = tileColor, modifier = Modifier.size(24.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(group.name, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(privacyIcon, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(
                            "$privacyText · ${group.memberCount.toPersianDigits()} عضو",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                AvatarStack(group.memberInitials, group.memberCount)
            }

            // فعالیت بعدی
            val shape = RoundedCornerShape(12.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.background)
                    .border(1.dp, colors.outline, shape)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.CalendarToday, null,
                    tint = if (group.nextEventText != null) colors.secondary else colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    group.nextEventText ?: "فعالیت برنامه‌ریزی‌شده‌ای وجود ندارد",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (group.nextEventText != null) FontWeight.Bold else FontWeight.Normal,
                    color = if (group.nextEventText != null) colors.onSurface else colors.onSurfaceVariant
                )
            }

            // پیشرفت هفته
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تکمیل این هفته", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    Text(
                        "${(group.progress * 100).roundToInt().toPersianDigits()}٪",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
                ChallengeProgressBar(group.progress, extra.brandGradient)
            }

            // باز کردن
            val btnShape = RoundedCornerShape(14.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(btnShape)
                    .background(colors.secondary.copy(alpha = 0.12f))
                    .border(1.dp, colors.secondary.copy(alpha = 0.5f), btnShape)
                    .clickable(onClick = onOpen),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("باز کردن گروه", color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowRight, null,
                    tint = colors.onSurface, modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AvatarStack(initials: List<String>, memberCount: Int) {
    val showOverflow = memberCount > 5
    val shown = if (showOverflow) initials.take(2) else initials.take(3)

    Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
        if (showOverflow) {
            Avatar("+${(memberCount - 2).toPersianDigits()}", 2, 30, fontSize = 10)
        }
        shown.forEachIndexed { i, letter -> Avatar(letter, i, 30) }
    }
}

@Composable
private fun Avatar(letter: String, colorIndex: Int, size: Int, fontSize: Int = 12) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(AvatarColors[colorIndex % AvatarColors.size])
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = OnAvatar, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
    }
}

// ───────────────────────── مهمان ─────────────────────────

@Composable
private fun GuestGroups(modifier: Modifier, onLogin: () -> Unit, onRegister: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val buttonShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("گروه‌ها", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // تصویر
            Box(modifier = Modifier.size(width = 170.dp, height = 130.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(extra.brandGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Box(Modifier.align(Alignment.TopCenter)) { Avatar("ر", 0, 40, 14) }
                Box(Modifier.align(Alignment.CenterStart).padding(start = 8.dp, bottom = 20.dp)) { Avatar("س", 1, 40, 14) }
                Box(Modifier.align(Alignment.CenterEnd).padding(end = 8.dp, bottom = 30.dp)) { Avatar("ع", 3, 36, 14) }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "برنامه‌ریزی گروهی، با ورود شروع می‌شود",
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "برای ساخت گروه، پیوستن با کد دعوت و دیدن برنامه هفتگی دوستانت باید وارد حساب شوی.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(buttonShape)
                    .background(extra.brandGradient)
                    .clickable(onClick = onLogin),
                contentAlignment = Alignment.Center
            ) {
                Text("ورود", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
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
                Text("ساخت حساب", color = colors.onSurface, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Outlined.Lock, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Text(
                    "گروه‌های خصوصی هیچ‌وقت عمومی نمایش داده نمی‌شوند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondary
                )
            }
        }
    }
}

// ───────────────────────── دیالوگ‌ها ─────────────────────────

@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, GroupPrivacy) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf(GroupPrivacy.PRIVATE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ساخت گروه جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم گروه") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = privacy == GroupPrivacy.PRIVATE,
                        onClick = { privacy = GroupPrivacy.PRIVATE },
                        label = { Text("خصوصی") }
                    )
                    FilterChip(
                        selected = privacy == GroupPrivacy.CODE,
                        onClick = { privacy = GroupPrivacy.CODE },
                        label = { Text("با کد دعوت") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, privacy) }) { Text("ساخت") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun JoinGroupDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("پیوستن با کد دعوت") },
        text = {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase().trim() },
                label = { Text("کد دعوت") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onJoin(code) }) { Text("پیوستن") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}