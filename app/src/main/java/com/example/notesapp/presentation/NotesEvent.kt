package com.example.notesapp.presentation

import com.example.notesapp.domain.model.Note

sealed class NotesEvent {
    data class CreateNoteEvent(val note: String): NotesEvent()
    data class DeleteNoteEvent(val note: Note): NotesEvent()
    object UpdateNoteEvent: NotesEvent()
}
