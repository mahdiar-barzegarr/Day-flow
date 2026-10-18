package com.example.dayflow.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val DfBackground = Color(0xFF080D18)
val DfSurface = Color(0xFF101827)
val DfCard = Color(0xFF151F32)
val DfOutline = Color(0xFF1F2C45)

val DfPrimary = Color(0xFF7865FF)
val DfSecondary = Color(0xFF438BFF)
val DfSuccess = Color(0xFF27D99A)
val DfHighlight = Color(0xFFFFB454)
val DfDanger = Color(0xFFFF6B7A)

val DfOnSurface = Color(0xFFF4F6FC)
val DfOnSurfaceVariant = Color(0xFF9AA7BC)

// فقط برای دکمه‌ی اصلی و حلقه‌ی پیشرفت
val DfBrandGradient = Brush.linearGradient(listOf(DfPrimary, DfSecondary))