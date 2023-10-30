package com.example.notesapp.domain.repository

import com.example.notesapp.data.repository.DBResult
import com.example.notesapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

abstract class NotesRepository {
    abstract suspend fun createNote(note: String): Flow<DBResult<Note>>
    abstract suspend fun queryNotes(): Flow<DBResult<List<Note>>>
    abstract suspend fun updateNote(note: Note): Flow<DBResult<Any>>
    abstract suspend fun deleteNote(note: Note): Flow<DBResult<Any>>
}