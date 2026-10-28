package com.example.dayflow.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.note.NoteEntity
import com.example.dayflow.di.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NoteEditorState(
    val title: String = "",
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val saved: Boolean = false,
    val isExisting: Boolean = false
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteEditorState())
    val state = _state.asStateFlow()

    private var noteId = 0L
    private var createdAt = System.currentTimeMillis()
    private var saveJob: Job? = null

    init {
        val id = savedStateHandle.get<Long>("noteId") ?: -1L
        if (id > 0) {
            viewModelScope.launch {
                repository.getAllNotes().first().find { it.id == id }?.let { note ->
                    noteId = note.id
                    createdAt = note.createdAt
                    _state.value = NoteEditorState(
                        title = note.title,
                        content = note.content,
                        updatedAt = note.updatedAt,
                        saved = true,
                        isExisting = true
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = edit { it.copy(title = value) }
    fun onContentChange(value: String) = edit { it.copy(content = value) }

    private fun edit(change: (NoteEditorState) -> NoteEditorState) {
        _state.update { change(it).copy(saved = false) }
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(800)
            persist()
        }
    }

    suspend fun saveNow() {
        saveJob?.cancel()
        persist()
    }

    suspend fun delete() {
        saveJob?.cancel()
        if (noteId > 0) {
            val s = _state.value
            repository.deleteNote(
                NoteEntity(
                    id = noteId,
                    title = s.title,
                    content = s.content,
                    createdAt = createdAt,
                    updatedAt = s.updatedAt
                )
            )
        }
    }

    private suspend fun persist() {
        val s = _state.value
        if (s.title.isBlank() && s.content.isBlank()) return
        val now = System.currentTimeMillis()

        withContext(NonCancellable) {
            val newId = repository.saveNote(
                NoteEntity(
                    id = noteId,
                    title = s.title.trim(),
                    content = s.content.trim(),
                    createdAt = createdAt,
                    updatedAt = now
                )
            )
            if (noteId == 0L) noteId = newId
        }
        _state.update { it.copy(saved = true, updatedAt = now, isExisting = true) }
    }
}