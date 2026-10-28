package com.example.dayflow.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dayflow.data.local.note.NoteEntity
import com.example.dayflow.di.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val notes = combine(
        repository.getAllNotes(),
        searchQuery
    ) { allNotes, query ->

        if (query.isBlank()) {
            allNotes
        } else {
            allNotes.filter { note ->
                note.title.contains(query.trim(), ignoreCase = true) ||
                        note.content.contains(query.trim(), ignoreCase = true)
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun saveNote(
        id: Long = 0,
        title: String,
        content: String
    ) {
        if (title.isBlank() && content.isBlank()) return

        viewModelScope.launch {
            val existingNote = notes.value.find { it.id == id }

            repository.saveNote(
                NoteEntity(
                    id = id,
                    title = title.trim(),
                    content = content.trim(),
                    createdAt = existingNote?.createdAt
                        ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}