package com.example.dayflow.ui.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dayflow.data.local.note.NoteEntity
import com.example.dayflow.ui.components.DayFlowBottomBar
import com.example.dayflow.ui.theme.DayFlowTheme
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDate
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    var search by remember { mutableStateOf("") }
    var deletingNote by remember { mutableStateOf<NoteEntity?>(null) }

    DayFlowTheme {
        val colors = MaterialTheme.colorScheme
        val isEmptyState = notes.isEmpty() && search.isBlank()

        fun openEditor(id: Long? = null) {
            navController.navigate(
                if (id == null) "note_editor" else "note_editor?noteId=$id"
            )
        }

        Scaffold(
            containerColor = colors.background,
            bottomBar = { DayFlowBottomBar(navController = navController) },
            floatingActionButton = {
                if (!isEmptyState) {
                    GradientButton(
                        text = "یادداشت جدید",
                        icon = Icons.Default.Add,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(bottom = 12.dp),
                        onClick = { openEditor() }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "یادداشت‌ها",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface
                        )
                        Text(
                            text = "${notes.size.toFaDigits()} یادداشت",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.surfaceVariant)
                            .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday, null,
                            tint = LocalExtraColors.current.highlight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = System.currentTimeMillis().toPersianDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurface
                        )
                    }
                }

                when {
                    isEmptyState -> NotesEmptyState(
                        onCreate = { openEditor() },
                        modifier = Modifier.weight(1f)
                    )

                    else -> {
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = search,
                            onValueChange = {
                                search = it
                                viewModel.onSearchChange(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "جستجو در عنوان و متن یادداشت‌ها",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, "جستجو")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.outline,
                                unfocusedBorderColor = colors.outline,
                                focusedContainerColor = colors.surface,
                                unfocusedContainerColor = colors.surface,
                                focusedTextColor = colors.onSurface,
                                unfocusedTextColor = colors.onSurface,
                                focusedPlaceholderColor = colors.onSurfaceVariant,
                                unfocusedPlaceholderColor = colors.onSurfaceVariant,
                                focusedLeadingIconColor = colors.onSurfaceVariant,
                                unfocusedLeadingIconColor = colors.onSurfaceVariant,
                                cursorColor = colors.primary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                        )

                        if (notes.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "یادداشتی پیدا نشد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        } else {
                            val grouped = remember(notes) {
                                notes.groupBy { dayLabel(it.updatedAt) }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                contentPadding = PaddingValues(bottom = 96.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                grouped.forEach { (label, group) ->
                                    item(key = "header_$label") {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = colors.onSurfaceVariant,
                                            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                                        )
                                    }
                                    items(group, key = { it.id }) { note ->
                                        NoteCard(
                                            note = note,
                                            onClick = { openEditor(note.id) },
                                            onLongClick = { deletingNote = note }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        deletingNote?.let { note ->
            AlertDialog(
                onDismissRequest = { deletingNote = null },
                containerColor = colors.surface,
                title = { Text("حذف یادداشت", color = colors.onSurface) },
                text = {
                    Text(
                        "از حذف «${note.title.ifBlank { "بدون عنوان" }}» مطمئنی؟",
                        color = colors.onSurfaceVariant
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteNote(note)
                        deletingNote = null
                    }) { Text("حذف", color = colors.error) }
                },
                dismissButton = {
                    TextButton(onClick = { deletingNote = null }) {
                        Text("انصراف", color = colors.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current
    val accent = listOf(colors.primary, colors.secondary, extra.success, extra.highlight)[(note.id % 4).toInt()]
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, shape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Description, null, tint = accent, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = note.title.ifBlank { "بدون عنوان" },
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.CalendarToday, null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = note.updatedAt.toPersianDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NotesEmptyState(onCreate: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NotesIllustration()
        Spacer(Modifier.height(20.dp))
        Text(
            "هنوز یادداشتی نداری",
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "هر چی تو ذهنته بنویس؛ ایده، لیست خرید، یا حال‌وهوای امروزت. همه‌چیز با تاریخ کنار هم می‌مونه.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TagChip("ایده‌ها", colors.primary)
            TagChip("لیست خرید", extra.highlight)
            TagChip("خاطره‌ی امروز", extra.success)
        }
        Spacer(Modifier.height(28.dp))
        GradientButton(
            text = "نوشتن اولین یادداشت",
            icon = Icons.Default.Add,
            onClick = onCreate
        )
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Composable
private fun NotesIllustration() {
    val colors = MaterialTheme.colorScheme
    val extra = LocalExtraColors.current

    Box(modifier = Modifier.size(width = 200.dp, height = 168.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = 112.dp.toPx()
            val h = 132.dp.toPx()
            val left = (size.width - w) / 2
            val top = 14.dp.toPx()
            val corner = CornerRadius(20.dp.toPx())
            val stroke = Stroke(2.dp.toPx())

            fun card(dy: Float, deg: Float, fill: Color, border: Color) {
                rotate(deg) {
                    drawRoundRect(fill, Offset(left, top + dy), Size(w, h), corner)
                    drawRoundRect(border, Offset(left, top + dy), Size(w, h), corner, style = stroke)
                }
            }
            card(12.dp.toPx(), -8f, colors.surface, colors.outline)
            card(6.dp.toPx(), 6f, colors.surfaceVariant, colors.outline)
            card(0f, 0f, colors.surfaceVariant, colors.primary)

            val x = left + 20.dp.toPx()
            val r = CornerRadius(4.dp.toPx())
            drawRoundRect(colors.primary, Offset(x, top + 24.dp.toPx()), Size(48.dp.toPx(), 8.dp.toPx()), r)
            listOf(72f, 60f, 68f).forEachIndexed { i, width ->
                drawRoundRect(
                    colors.outline,
                    Offset(x, top + (48 + i * 16).dp.toPx()),
                    Size(width.dp.toPx(), 6.dp.toPx()),
                    CornerRadius(3.dp.toPx())
                )
            }
            drawCircle(extra.highlight, 4.dp.toPx(), Offset(34.dp.toPx(), 48.dp.toPx()))
            drawCircle(extra.success, 3.dp.toPx(), Offset(170.dp.toPx(), 40.dp.toPx()))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 4.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.background)
                .border(2.dp, colors.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Edit, null, tint = colors.secondary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
internal fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(LocalExtraColors.current.brandGradient)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

internal fun Int.toFaDigits(): String =
    toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")

private fun dayLabel(timestamp: Long): String {
    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val day = 24L * 60 * 60 * 1000
    return when {
        timestamp >= startOfToday -> "امروز"
        timestamp >= startOfToday - day -> "دیروز"
        else -> timestamp.toPersianDate()
    }
}