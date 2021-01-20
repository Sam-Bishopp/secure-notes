package com.example.securenotes.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.securenotes.database.Note

@Dao
interface NoteDao
{
    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE title LIKE :searchQuery OR subtitle LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchNotes(searchQuery: String) : LiveData<List<Note>>

    @Insert
    fun insertNote(note : Note)

    @Update
    fun updateNote(note : Note)

    @Delete
    fun deleteNote(note : Note)
}