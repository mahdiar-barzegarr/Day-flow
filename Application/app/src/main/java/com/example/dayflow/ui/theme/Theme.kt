package com.example.dayflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DayFlowDarkColorScheme = darkColorScheme(
    primary = DfPrimary,
    onPrimary = Color.White,
    secondary = DfSecondary,
    onSecondary = Color.White,
    tertiary = DfSuccess,
    background = DfBackground,
    onBackground = DfOnSurface,
    surface = DfSurface,
    onSurface = DfOnSurface,
    surfaceVariant = DfCard,
    onSurfaceVariant = DfOnSurfaceVariant,
    outline = DfOutline,
    error = DfDanger,
    onError = Color.White
)

@Immutable
data class DayFlowExtraColors(
    val success: Color = DfSuccess,
    val highlight: Color = DfHighlight,
    val brandGradient: Brush = DfBrandGradient
)

val LocalExtraColors = staticCompositionLocalOf { DayFlowExtraColors() }

@Composable
fun DayFlowTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalExtraColors provides DayFlowExtraColors(),
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            colorScheme = DayFlowDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}