package com.example.dayflow.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dayflow.ui.challenges.FlowSurface
import com.example.dayflow.ui.theme.LocalExtraColors
import kotlinx.coroutines.delay

@Composable
fun AboutScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    val infinite = rememberInfiniteTransition(label = "about")
    val pulse by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotation by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "rotation"
    )

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .statusBarsPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton({
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack, null,
                        tint = colors.onSurface,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text("درباره", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
            }

            AppearIn(0) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .drawBehind {
                            rotate(rotation) {
                                drawCircle(
                                    brush = extra.brandGradient,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 18f))
                                    )
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(pulse)
                            .clip(CircleShape)
                            .background(extra.brandGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Bolt, null, tint = Color.White, modifier = Modifier.size(52.dp))
                    }
                }
            }

            AppearIn(1) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("دی‌فلو", style = MaterialTheme.typography.headlineLarge, color = colors.onSurface)
                    Text(
                        "هر روز یه قدم کوچک، یه هدف بزرگ.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AppearIn(2) {
                FlowSurface {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FeatureRow(Icons.Outlined.CheckCircle, "عادت‌ها", "هر روز تیک بزن و استریکت را زنده نگه دار", extra.highlight)
                        FeatureRow(Icons.Outlined.EmojiEvents, "چالش‌ها", "برای خودت هدف زمان‌دار بساز و پیشرفتت را ببین", extra.success)
                        FeatureRow(Icons.Outlined.EditNote, "نت‌ها", "ایده‌ها و برنامه‌هایت همیشه دم‌دست", colors.secondary)
                        FeatureRow(Icons.Outlined.Notifications, "یادآورها", "دیگر هیچ کاری از یادت نمی‌رود", extra.highlight)
                    }
                }
            }

            AppearIn(3) {
                FlowSurface {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("نسخه", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                        Text("۱٫۰٫۰", style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                    }
                }
            }

            AppearIn(4) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ساخته‌شده با", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    Icon(
                        Icons.Outlined.Favorite, null,
                        tint = colors.error,
                        modifier = Modifier.size(16.dp).scale(pulse)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, desc: String, tint: Color) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp)) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun AppearIn(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 130L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
    ) { content() }
}