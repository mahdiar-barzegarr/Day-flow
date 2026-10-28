package com.example.dayflow.ui.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dayflow.ui.theme.DayFlowTheme
import com.example.dayflow.ui.theme.LocalExtraColors
import com.example.dayflow.utilities.toPersianDate
import kotlinx.coroutines.launch

@Composable
fun NoteEditorScreen(
    onBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var confirmDelete by remember { mutableStateOf(false) }

    fun close() {
        scope.launch {
            viewModel.saveNow()
            onBack()
        }
    }
    BackHandler { close() }

    DayFlowTheme {
        val colors = MaterialTheme.colorScheme
        val extra = LocalExtraColors.current
        val cardShape = RoundedCornerShape(20.dp)
        val wordCount = remember(state.content) {
            state.content.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SquareIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    description = "بازگشت",
                    tint = colors.onSurface,
                    background = colors.surfaceVariant,
                    border = colors.outline,
                    onClick = { close() }
                )
                Text(
                    text = if (state.isExisting) "ویرایش یادداشت" else "یادداشت جدید",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                )
                if (state.isExisting) {
                    SquareIconButton(
                        icon = Icons.Outlined.DeleteOutline,
                        description = "حذف یادداشت",
                        tint = colors.error,
                        background = colors.error.copy(alpha = 0.12f),
                        border = colors.error.copy(alpha = 0.28f),
                        onClick = { confirmDelete = true }
                    )
                }
            }

            // Date + autosave status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.CalendarToday, null,
                        tint = extra.highlight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.updatedAt.toPersianDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface
                    )
                }
                if (state.saved) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Check, null,
                            tint = extra.success,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ذخیره خودکار شد",
                            style = MaterialTheme.typography.bodySmall,
                            color = extra.success
                        )
                    }
                }
            }

            // Writing card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.outline, cardShape)
                    .padding(20.dp)
            ) {
                BasicTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge.copy(color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (state.title.isEmpty()) {
                                Text(
                                    "عنوان یادداشت",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            inner()
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 14.dp)
                        .width(40.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.primary)
                )

                BasicTextField(
                    value = state.content,
                    onValueChange = viewModel::onContentChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.onSurface,
                        lineHeight = 26.sp
                    ),
                    cursorBrush = SolidColor(colors.primary),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (state.content.isEmpty()) {
                                Text(
                                    "هر چی تو ذهنته اینجا بنویس…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            inner()
                        }
                    }
                )

                HorizontalDivider(color = colors.outline)
                Text(
                    text = "${wordCount.toFaDigits()} کلمه",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            GradientButton(
                text = "ذخیره یادداشت",
                icon = Icons.Default.Check,
                modifier = Modifier.fillMaxWidth(),
                onClick = { close() }
            )
        }

        if (confirmDelete) {
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                containerColor = colors.surface,
                title = { Text("حذف یادداشت", color = colors.onSurface) },
                text = {
                    Text("این یادداشت برای همیشه پاک می‌شه. مطمئنی؟", color = colors.onSurfaceVariant)
                },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                        scope.launch {
                            viewModel.delete()
                            onBack()
                        }
                    }) { Text("حذف", color = colors.error) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDelete = false }) {
                        Text("انصراف", color = colors.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
private fun SquareIconButton(
    icon: ImageVector,
    description: String,
    tint: Color,
    background: Color,
    border: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(shape)
            .background(background)
            .border(1.dp, border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = tint, modifier = Modifier.size(22.dp))
    }
}