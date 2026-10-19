package com.example.dayflow.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.dayflow.R

// res/font/estedad_bold.ttf  و  res/font/vazirmatn_light.ttf
val Estedad = FontFamily(Font(R.font.estedad_bold, FontWeight.Bold))
val Vazirmatn = FontFamily(Font(R.font.vazirmatn_light, FontWeight.Light))


val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 44.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 38.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 26.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Estedad,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
