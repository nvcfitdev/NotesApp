package com.example.notesapp.data.repository

import com.example.notesapp.data.local.NotesDatabase
import com.example.notesapp.data.mapper.toEntity
import com.example.notesapp.data.mapper.toNoteList
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDatabase
) : NotesRepository() {

    private val dao = notesDao.dao()

    override suspend fun createNote(note: String): Flow<DBResult<Note>> {
        return flow {
            emit(DBResult.Loading(isLoading = true))
            dao.insertNote(Note(id = 0, note = note).toEntity())
            emit(DBResult.Success(isLoading = false))
        }
    }

    override suspend fun queryNotes(): Flow<DBResult<List<Note>>> {
        return flow {
            emit(DBResult.Loading(isLoading = true))
            val result = dao.queryNotes()
            emit(DBResult.Success(data = result.toNoteList(), isLoading = false))
        }
    }

    override suspend fun updateNote(note: Note): Flow<DBResult<Any>> {
        return flow {
            emit(DBResult.Loading(isLoading = true))
            dao.updateNote(note.toEntity())
            emit(DBResult.Success(isLoading = false))
        }
    }

    override suspend fun deleteNote(note: Note): Flow<DBResult<Any>> {
        return flow {
            emit(DBResult.Loading(isLoading = true))
            dao.deleteNote(note.id)
            emit(DBResult.Success(isLoading = false))
        }
    }
}

sealed class DBResult<T>(
    val data: T? = null,
    val isError: Boolean = false,
    val isLoading: Boolean
) {
    class Success<T>(data: T? = null,  isError: Boolean = false, isLoading: Boolean = false):
            DBResult<T>(data, isError, isLoading)
    class Error<T>(data: T? = null,  isError: Boolean = true, isLoading: Boolean = false):
        DBResult<T>(data, isError, isLoading)
    class Loading<T>(data: T? = null,  isError: Boolean = false, isLoading: Boolean = true):
        DBResult<T>(data, isError, isLoading)
}