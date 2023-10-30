package com.example.notesapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.repository.DBResult
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    var state by mutableStateOf(NotesState())

    init {
        queryNotes()
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.CreateNoteEvent -> {
                createNote(event.note)
                queryNotes()
            }
            is NotesEvent.DeleteNoteEvent -> {
                deleteNote(event.note)
            }
            is NotesEvent.UpdateNoteEvent -> {

            }
        }
    }

    fun queryNotes() {
        viewModelScope.launch {
            repository.queryNotes()
                .collect { result ->
                    state = when (result) {
                        is DBResult.Success -> {
                            state.copy(noteList = result.data as MutableList<Note>, isLoading = false)
                        }
                        is DBResult.Error -> state.copy(isError = "You don't have any notes saved!")
                        is DBResult.Loading -> state.copy(isLoading = true)
                    }
                }
        }
    }

    fun createNote(note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val save = async { repository.createNote(note) }.await()
            save.collect { result ->
                state = when (result) {
                    is DBResult.Success -> state.copy(isSaved = true, isLoading = false)
                    is DBResult.Error -> state.copy(isError = "Note wasn't saved..")
                    is DBResult.Loading -> state.copy(isLoading = true)
                }
                queryNotes()
            }
        }
    }

    fun updateNotes(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
                .collect { result ->
                    state = when (result) {
                        is DBResult.Success -> state.copy(isSuccess = true, isLoading = false)
                        is DBResult.Error -> state.copy(isError = "Note wasn't saved..")
                        is DBResult.Loading -> state.copy(isLoading = true)
                    }
                }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
                .collect { result ->
                    state = when (result) {
                        is DBResult.Success -> state.copy(isSuccess = true, isLoading = false)
                        is DBResult.Error -> state.copy(isError = "Note wasn't saved..")
                        is DBResult.Loading -> state.copy(isLoading = true)
                    }
                }
        }
    }
}