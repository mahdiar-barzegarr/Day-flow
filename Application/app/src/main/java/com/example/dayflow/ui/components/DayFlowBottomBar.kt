package com.example.dayflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * تب‌های نوار پایین. مقدار route باید با route های NavGraph یکی باشه.
 */
enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "خانه", Icons.Outlined.Home),
    Challenges("challenges", "چالش‌ها", Icons.Outlined.Flag),
    Groups("groups", "گروه‌ها", Icons.Outlined.Groups),
    Friends("friends", "دوستان", Icons.Outlined.PersonAdd),
    Profile("profile", "پروفایل", Icons.Outlined.Person)
}

/**
 * نوار پایین مشترک همه‌ی صفحه‌ها. استفاده:
 *
 * Scaffold(bottomBar = { DayFlowBottomBar(navController) }) { ... }
 */
@Composable
fun DayFlowBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    BottomBarContent(
        isSelected = { tab -> destination?.hierarchy?.any { it.route == tab.route } == true },
        onTabClick = { tab ->
            navController.navigate(tab.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
private fun BottomBarContent(
    isSelected: (BottomTab) -> Boolean,
    onTabClick: (BottomTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind {
                drawLine(
                    color = colors.outline,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomTab.entries.forEach { tab ->
            BottomBarItem(
                tab = tab,
                selected = isSelected(tab),
                onClick = { onTabClick(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val contentColor = if (selected) colors.onSurface else colors.onSurfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected) colors.primary.copy(alpha = 0.22f) else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = tab.label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
