package com.example.dayflow.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dayflow.ui.theme.LocalExtraColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val SPLASH_DURATION_MS = 3500
private const val SPLASH_END_DELAY_MS = 250L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(SPLASH_DURATION_MS, easing = LinearEasing)
        )
        delay(SPLASH_END_DELAY_MS.milliseconds)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .drawBehind {
                // درخشش ملایم بنفش پشت لوگو
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(colors.primary.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height * 0.4f),
                        radius = size.maxDimension * 0.55f
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // آیکون لوگو
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(extra.brandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // اسم اپ: کلمه‌ی اول سفید، کلمه‌ی دوم آبی
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.onSurface)) { append("Day") }
                    withStyle(SpanStyle(color = colors.secondary)) { append("Flow") }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "قدم‌های کوچک، هدف‌های بزرگ",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        // نوار لودینگ پایین صفحه
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 64.dp)
                .width(180.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.outline)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .clip(RoundedCornerShape(50))
                    .background(extra.brandGradient)
            )
        }
    }
}