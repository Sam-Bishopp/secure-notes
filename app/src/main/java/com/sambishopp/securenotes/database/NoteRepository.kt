package com.sambishopp.securenotes.database

import android.app.Application
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class NoteRepository(application: Application) : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var noteDao: NoteDao?

    init
    {
        val database = NotesDatabase.getDatabase(application)
        noteDao = database.noteDao()
    }

    fun getAllNotes() = noteDao?.getAllNotes()

    fun searchNotes(searchQuery: String) = noteDao?.searchNotes(searchQuery)

    fun insertNote(note: Note)
    {
        launch { insertNoteBG(note) }
    }

    fun updateNote(note: Note)
    {
        launch { updateNoteBG(note) }
    }

    fun deleteNote(note: Note)
    {
        launch { deleteNoteBG(note) }
    }

    private suspend fun insertNoteBG(note: Note)
    {
        withContext(Dispatchers.IO)
        {
            noteDao?.insertNote(note)
        }
    }

    private suspend fun updateNoteBG(note: Note)
    {
        withContext(Dispatchers.IO)
        {
            noteDao?.updateNote(note)
        }
    }

    private suspend fun deleteNoteBG(note: Note)
    {
        withContext(Dispatchers.IO)
        {
            noteDao?.deleteNote(note)
        }
    }
}