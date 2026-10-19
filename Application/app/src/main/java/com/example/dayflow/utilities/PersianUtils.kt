package com.example.dayflow.utilities

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/** روزهای هفته به ترتیب شروع از شنبه. */
val persianWeekLabels = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

private val persianLocale = ULocale("fa_IR@calendar=persian")

fun Int.toPersianDigits(): String =
    toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDate.toDate(): Date =
    Date.from(atStartOfDay(ZoneId.systemDefault()).toInstant())

/** مثلاً «چهارشنبه ۲۹ مهر ۱۴۰۵» */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toPersianFullDate(): String =
    SimpleDateFormat("EEEE d MMMM y", persianLocale).format(toDate())

/** روز ماه شمسی (عدد ۱ تا ۳۱) */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.persianDayOfMonth(): Int {
    val calendar = Calendar.getInstance(persianLocale)
    calendar.time = toDate()
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun LocalDate.toPersianDayMonth(): String =
    SimpleDateFormat("d MMMM", persianLocale).format(toDate())

fun LocalDate.toPersianDayMonthYear(): String =
    SimpleDateFormat("d MMMM y", persianLocale).format(toDate())
