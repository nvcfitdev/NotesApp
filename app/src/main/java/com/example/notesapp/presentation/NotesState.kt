package com.example.notesapp.presentation

import com.example.notesapp.domain.model.Note

data class NotesState(
    val noteList: MutableList<Note> = mutableListOf(),
    val isSuccess: Boolean = false,
    val isSaved: Boolean = false,
    val isError: String = "",
    val isLoading: Boolean = false,
    val note: String = ""
)