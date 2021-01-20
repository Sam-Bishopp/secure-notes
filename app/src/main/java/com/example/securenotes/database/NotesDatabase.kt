package com.example.securenotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class NotesDatabase : RoomDatabase()
{
    abstract fun noteDao(): NoteDao

    companion object
    {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context : Context): NotesDatabase
        {
            val tempInstance = INSTANCE

            if(tempInstance != null)
            {
                return tempInstance
            }

            synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
