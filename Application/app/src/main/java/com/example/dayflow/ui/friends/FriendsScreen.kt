package com.example.dayflow.ui.friends


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.dayflow.models.FriendRequest
import com.example.dayflow.models.FriendStatus
import com.example.dayflow.models.FriendsUiState
import com.example.dayflow.models.UserResult
import com.example.dayflow.ui.components.DayFlowBottomBar

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel,
    navController: NavController,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { DayFlowBottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "دوستان",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))

            if (!state.isLoggedIn) GuestContent(onLoginClick, onRegisterClick)
            else LoggedInContent(state, viewModel)
        }
    }
}

// ───────── مهمان ─────────
@Composable
private fun GuestContent(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(96.dp).clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.PersonAdd, null, Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(24.dp))
        Text("دوستانت را با نام کاربری پیدا کن",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        Text(
            "برای افزودن دوست و دعوت آن‌ها به گروه، ابتدا وارد حساب شو. ایمیل و شماره تماس هیچ‌وقت نمایش داده نمی‌شود.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        GradientButton("ورود", onLogin)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("ساخت حساب", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(Modifier.height(16.dp))
        PrivacyNote()
    }
}

// ───────── کاربر واردشده ─────────
@Composable
private fun LoggedInContent(state: FriendsUiState, vm: FriendsViewModel) {
    TabsRow(state, vm::onTabChange)
    Spacer(Modifier.height(16.dp))

    when (state.selectedTab) {
        0 -> EmptyHint(Icons.Outlined.People, "هنوز دوستی اضافه نکرده‌ای",
            "از تب «افزودن» با نام کاربری دوستانت را پیدا کن.")
        1 -> if (state.requests.isEmpty())
            EmptyHint(Icons.Outlined.MarkEmailRead, "درخواستی نداری", "درخواست‌های دریافتی اینجا نمایش داده می‌شود.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.requests, key = { it.id }) { RequestCard(it, vm::respond) }
        }
        else -> AddTab(state, vm)
    }
}

@Composable
private fun TabsRow(state: FriendsUiState, onTab: (Int) -> Unit) {
    val reqCount = state.requests.size
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ترتیب در RTL: دوستان (راست) ← درخواست‌ها ← افزودن (چپ)
        TabItem("دوستان ${state.friendsCount}", state.selectedTab == 0, Modifier.weight(1f)) { onTab(0) }
        TabItem("درخواست‌ها", state.selectedTab == 1, Modifier.weight(1f), badge = reqCount) { onTab(1) }
        TabItem("افزودن", state.selectedTab == 2, Modifier.weight(1f)) { onTab(2) }
    }
}

@Composable
private fun TabItem(
    title: String, selected: Boolean, modifier: Modifier,
    badge: Int = 0, onClick: () -> Unit
) {
    val bg = if (selected)
        Modifier.background(Brush.horizontalGradient(
            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
    else Modifier
    Row(
        modifier.height(44.dp).clip(RoundedCornerShape(12.dp)).then(bg).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
        if (badge > 0) {
            Spacer(Modifier.width(6.dp))
            Box(Modifier.size(18.dp).clip(CircleShape).background(Color(0xFFFFB454)),
                contentAlignment = Alignment.Center) {
                Text("$badge", style = MaterialTheme.typography.labelSmall, color = Color.Black)
            }
        }
    }
}

@Composable
private fun AddTab(state: FriendsUiState, vm: FriendsViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("@نام‌کاربری", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                trailingIcon = if (state.isSearching) {
                    { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) }
                } else null,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            Spacer(Modifier.height(8.dp))
            PrivacyNote()
        }

        if (state.query.isNotBlank() && !state.isSearching) {
            item {
                Text(
                    if (state.results.isEmpty()) "کاربری با این نام پیدا نشد"
                    else "نتایج جستجو · ${state.results.size} نتیجه",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (state.results.isNotEmpty()) item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    state.results.forEachIndexed { i, u ->
                        UserRow(u) { vm.sendRequest(u) }
                        if (i != state.results.lastIndex)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        if (state.requests.isNotEmpty()) {
            item {
                Text("درخواست‌های دریافتی", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            items(state.requests, key = { "r${it.id}" }) { RequestCard(it, vm::respond) }
        }
    }
}

@Composable
private fun UserRow(user: UserResult, onAdd: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Avatar(user.name)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(user.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            if (user.mutualGroups > 0)
                Text("${user.mutualGroups} گروه مشترک", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        when (user.status) {
            FriendStatus.NONE -> Button(onClick = onAdd, shape = RoundedCornerShape(12.dp)) {
                Text("افزودن دوست", style = MaterialTheme.typography.labelLarge)
            }
            FriendStatus.REQUEST_SENT -> StatusChip(Icons.Outlined.Schedule, "درخواست ارسال شد", Color(0xFFFFB454))
            FriendStatus.FRIEND -> StatusChip(Icons.Outlined.Check, "دوست شما", Color(0xFF27D99A))
        }
    }
}

@Composable
private fun RequestCard(req: FriendRequest, onRespond: (FriendRequest, Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(req.name)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(req.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("@${req.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text(req.mutualInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onRespond(req, true) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF27D99A), Color.Black)
                ) { Text("پذیرفتن", style = MaterialTheme.typography.labelLarge) }
                OutlinedButton(
                    onClick = { onRespond(req, false) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) { Text("رد کردن", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface) }
            }
        }
    }
}

// ───────── اجزای کوچک ─────────
@Composable
private fun Avatar(name: String) {
    Box(Modifier.size(48.dp).clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center) {
        Text(name.firstOrNull()?.toString() ?: "؟",
            style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun StatusChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(
        Modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = color)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
private fun PrivacyNote() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Shield, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(6.dp))
        Text("فقط با نام کاربری جستجو می‌شود.", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyHint(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Column(Modifier.fillMaxWidth().padding(top = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text(desc, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}