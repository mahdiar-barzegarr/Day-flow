package com.example.dayflow.ui.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dayflow.data.local.ChallengeStatus
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.home.SheetChip
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDigits
import kotlin.math.roundToInt

@Composable
fun ChallengesScreen(
    navController: NavController,
    onChallengeClick: (Long) -> Unit,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { DayFlowBottomBar(navController) },
        floatingActionButton = { NewChallengeButton { showCreate = true } },
        floatingActionButtonPosition = FabPosition.Start // در RTL یعنی سمت راست
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ChallengesTopBar(state, viewModel::toggleSearch, viewModel::setQuery)
            TabsRow(state, viewModel::setTab)
            FilterRow(state.filter, viewModel::setFilter)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (state.items.isEmpty()) {
                    EmptyState(state)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            ChallengeCard(
                                item = item,
                                onClick = { onChallengeClick(item.id) },
                                onActivate = { viewModel.activate(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        CreateChallengeSheet(
            onDismiss = { showCreate = false },
            onCreate = viewModel::createChallenge
        )
    }
}

@Composable
private fun ChallengesTopBar(
    state: ChallengesUiState,
    onToggleSearch: () -> Unit,
    onQuery: (String) -> Unit
) {
    val focus = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .heightIn(min = 52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.searchOpen) {
            LaunchedEffect(Unit) { focus.requestFocus() }
            OutlinedTextField(
                value = state.query,
                onValueChange = onQuery,
                singleLine = true,
                placeholder = { Text("جستجوی چالش", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                trailingIcon = {
                    IconButton(onClick = onToggleSearch) { Icon(Icons.Outlined.Close, "بستن جستجو") }
                },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = challengeFieldColors(),
                modifier = Modifier.weight(1f).focusRequester(focus)
            )
        } else {
            Text(
                "چالش‌ها",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            TopIconButton(Icons.Outlined.Search, "جستجو", onToggleSearch)
        }
    }
}

@Composable
private fun TabsRow(state: ChallengesUiState, onTab: (ChallengeTab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val shape = RoundedCornerShape(16.dp)
    val tabs = listOf(
        Triple(ChallengeTab.Active, "فعال", state.activeCount),
        Triple(ChallengeTab.Upcoming, "آینده", state.upcomingCount),
        Triple(ChallengeTab.Completed, "تمام‌شده", state.completedCount)
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.outline, shape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { (tab, label, count) ->
            val selected = state.tab == tab
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(if (selected) Modifier.background(extra.brandGradient) else Modifier)
                    .clickable { onTab(tab) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else colors.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    count.toPersianDigits(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) Color.White.copy(alpha = 0.85f) else colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FilterRow(selected: TypeFilter, onFilter: (TypeFilter) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SheetChip("همه", selected == TypeFilter.All) { onFilter(TypeFilter.All) }
        SheetChip("شخصی", selected == TypeFilter.Personal) { onFilter(TypeFilter.Personal) }
        SheetChip("گروهی", selected == TypeFilter.Group) { onFilter(TypeFilter.Group) }
    }
}

@Composable
private fun ChallengeCard(item: ChallengeUi, onClick: () -> Unit, onActivate: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val barBrush = when (item.status) {
        ChallengeStatus.COMPLETED -> SolidColor(extra.success)
        ChallengeStatus.PAUSED -> SolidColor(extra.highlight)
        else -> extra.brandGradient
    }

    FlowSurface(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChallengeIconBox(item.iconKey)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.title, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday, null,
                            tint = colors.onSurfaceVariant, modifier = Modifier.size(12.dp)
                        )
                        val groupPart = if (item.isGroup && !item.groupName.isNullOrBlank()) " · ${item.groupName}" else ""
                        Text(
                            item.dateText + groupPart,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TypeBadge(item.isGroup)
                    StatusBadge(item.status)
                }
            }

            if (item.status != ChallengeStatus.UPCOMING) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${item.doneDays.toPersianDigits()} از ${item.totalDays.toPersianDigits()} روز",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            "${(item.progress * 100).roundToInt().toPersianDigits()}٪",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurface
                        )
                    }
                    ChallengeProgressBar(item.progress, barBrush)
                }
            }

            if (item.status == ChallengeStatus.ACTIVE && item.streak > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    StreakLabel(item.streak)
                }
            }

            if (item.status == ChallengeStatus.UPCOMING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(extra.brandGradient)
                            .clickable(onClick = onActivate)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("فعال‌سازی", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewChallengeButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LocalExtraColors.current.brandGradient)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("چالش جدید", style = MaterialTheme.typography.labelLarge, color = Color.White)
        Icon(Icons.Outlined.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun EmptyState(state: ChallengesUiState) {
    val colors = MaterialTheme.colorScheme
    val searching = state.query.isNotBlank() || state.filter != TypeFilter.All
    val (title, hint) = when {
        searching -> "چیزی پیدا نشد" to "عبارت یا فیلتر دیگه‌ای رو امتحان کن."
        state.tab == ChallengeTab.Active -> "هنوز چالش فعالی نداری" to "با دکمه‌ی «چالش جدید» یه چالش بساز."
        state.tab == ChallengeTab.Upcoming -> "چالش آینده‌ای نداری" to "چالشی که «بعداً فعال می‌کنم» رو بزنی اینجا میاد."
        else -> "هنوز چالشی تموم نشده" to "چالش‌هایی که به پایان برسن اینجا نشون داده می‌شن."
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (searching) Icons.Outlined.SearchOff else Icons.Outlined.Flag, null,
            tint = colors.onSurfaceVariant, modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(
            hint,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}