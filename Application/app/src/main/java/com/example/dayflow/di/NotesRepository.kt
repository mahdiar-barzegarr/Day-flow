package com.example.dayflow.di


import com.example.dayflow.data.local.note.NoteDao
import com.example.dayflow.data.local.note.NoteEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class NotesRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<NoteEntity>> =
        noteDao.getAllNotes()

    suspend fun saveNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }
}