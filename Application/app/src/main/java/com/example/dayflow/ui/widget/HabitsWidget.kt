package com.example.dayflow.ui.widget

import android.content.Context
import android.os.Build
import android.view.Surface
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.example.dayflow.R
import com.example.dayflow.base.habitRepo
import com.example.dayflow.utilities.toPersianDigits

data class WidgetHabit(val id: Long, val title: String, val done: Boolean)

val HabitIdKey = ActionParameters.Key<Long>("habit_id")

private val TextMain = Color(0xFFF4F6FC)
private val TextDim = Color(0xFF9AA7BC)
private val Divider = Color(0xFF1F2C45)
private val Success = Color(0xFF27D99A)
private val Bg = Color(0xFF080D18)
private val Surface = Color(0xFF101827)

private fun cp(c: Color) = ColorProvider(day = c, night = c)

class HabitsWidget : GlanceAppWidget() {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = context.habitRepo()
        provideContent {
            val habits by repo.observeTodayWidget().collectAsState(initial = emptyList())
            WidgetContent(habits)
        }
    }
}

@Composable
private fun WidgetContent(habits: List<WidgetHabit>) {
    val rtl = LocalContext.current.resources.configuration.layoutDirection ==
            View.LAYOUT_DIRECTION_RTL
    val doneCount = habits.count { it.done }

    val title: @Composable RowScope.() -> Unit = {
        Text(
            "عادت‌های امروز",
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = cp(TextMain),
                textAlign = if (rtl) TextAlign.Start else TextAlign.End
            )
        )
    }
    val counter: @Composable RowScope.() -> Unit = {
        Text(
            "${doneCount.toPersianDigits()}/${habits.size.toPersianDigits()}",
            style = TextStyle(fontSize = 12.sp, color = cp(Success))
        )
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cp(Surface))
            .cornerRadius(24.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rtl) {
                title(); counter()
            } else {
                counter(); title()
            }
        }

        if (habits.isEmpty()) {
            Text(
                "هنوز عادتی نداری",
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = cp(TextDim),
                    textAlign = if (rtl) TextAlign.Start else TextAlign.End
                )
            )
        } else {
            LazyColumn {
                items(habits, itemId = { it.id }) { habit -> HabitRow(habit, rtl) }
            }
        }
    }
}

@Composable
private fun HabitRow(habit: WidgetHabit, rtl: Boolean) {
    val check: @Composable RowScope.() -> Unit = {
        Box(
            modifier = GlanceModifier
                .size(24.dp)
                .background(
                    ImageProvider(
                        if (habit.done) R.drawable.widget_check_on else R.drawable.widget_check_off
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (habit.done) {
                Text(
                    "✓",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = cp(Bg)
                    )
                )
            }
        }
    }
    val label: @Composable RowScope.() -> Unit = {
        Text(
            habit.title,
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight().padding(horizontal = 10.dp),
            style = TextStyle(
                fontSize = 14.sp,
                color = cp(if (habit.done) TextDim else TextMain),
                textAlign = if (rtl) TextAlign.Start else TextAlign.End,
                textDecoration = if (habit.done) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }


    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(
                    actionRunCallback<ToggleHabitAction>(actionParametersOf(HabitIdKey to habit.id))
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rtl) {
                label(); check()
            } else {
                check(); label()
            }
        }
        Spacer(GlanceModifier.fillMaxWidth().height(1.dp).background(cp(Divider)))
    }
}

private fun iconColor(key: String) = when (key) {
    "water" -> Color(0xFF438BFF)               // DfSecondary
    "book" -> Color(0xFFFFB454)                // DfHighlight
    "fitness", "sleep" -> Color(0xFF7865FF)    // DfPrimary
    else -> Success
}

class ToggleHabitAction : ActionCallback {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val id = parameters[HabitIdKey] ?: return
        context.habitRepo().toggleToday(id)
    }
}

class HabitsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HabitsWidget()
}