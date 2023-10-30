package com.example.notesapp.data.mapper

import com.example.notesapp.data.local.NoteEntity
import com.example.notesapp.domain.model.Note

fun Note.toEntity(): NoteEntity =
    NoteEntity(
        id = null,
        message = this.note
    )

fun List<NoteEntity>.toNoteList(): List<Note> {
    val note = arrayListOf<Note>()
    this.map { entity ->
        note.add(Note(note = entity.message, id = entity.id!!))
    }
    return note
}