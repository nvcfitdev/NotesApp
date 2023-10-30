package com.example.notesapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(notes: NoteEntity): Long

    @Query("SELECT * FROM noteentity")
    suspend fun queryNotes() : List<NoteEntity>

    @Query("DELETE FROM noteentity WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Update
    suspend fun updateNote(note: NoteEntity)
}