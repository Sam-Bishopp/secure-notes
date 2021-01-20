package com.example.securenotes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @ColumnInfo(name = "title")
    var title : String?,

    @ColumnInfo(name = "subtitle")
    var subtitle : String?,

    @ColumnInfo(name = "description")
    var description : String?,

    @ColumnInfo(name = "date_time")
    var dateTime : String?,

    @PrimaryKey(autoGenerate = true)
    var id : Int
)