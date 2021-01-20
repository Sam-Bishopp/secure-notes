package com.example.securenotes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.securenotes.database.Note
import com.example.securenotes.database.NoteRepository

class NoteViewModel(application: Application) : AndroidViewModel(application)
{
    private var repository: NoteRepository = NoteRepository(application)

    fun getAllNotes() = repository.getAllNotes()

    fun searchNotes(searchQuery: String) = repository.searchNotes(searchQuery)

    fun insertNote(note: Note) { repository.insertNote(note) }

    fun updateNote(note: Note) { repository.updateNote(note) }

    fun deleteNote(note: Note) { repository.deleteNote(note) }
}