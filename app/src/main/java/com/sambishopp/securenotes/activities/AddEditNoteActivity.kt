package com.sambishopp.securenotes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sambishopp.securenotes.R
import kotlinx.android.synthetic.main.activity_add_note.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteActivity : AppCompatActivity()
{
    companion object
    {
        const val EXTRA_TITLE: String = "com.example.securenotes.activities.EXTRA_TITLE"
        const val EXTRA_SUBTITLE: String = "com.example.securenotes.activities.EXTRA_SUBTITLE"
        const val EXTRA_DESCRIPTION: String = "com.example.securenotes.activities.EXTRA_DESCRIPTION"
        const val EXTRA_DATE_TIME: String = "com.example.securenotes.activities.EXTRA_DATE_TIME"
        const val EXTRA_ID: String = "com.example.securenotes.activities.EXTRA_ID"
    }

    private lateinit var addActivityTitleText: TextView
    private lateinit var noteTitleText: EditText
    private lateinit var noteDateTimeText: TextView
    private lateinit var noteSubtitleText: EditText
    private lateinit var noteDescriptionText: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorCardView)
        setContentView(R.layout.activity_add_note)

        addActivityTitleText = findViewById(R.id.addActivityTitle)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        noteTitleText = findViewById(R.id.noteTitle)
        noteSubtitleText = findViewById(R.id.noteSubtitle)
        noteDescriptionText = findViewById(R.id.noteDescription)
        noteDateTimeText = findViewById(R.id.noteDateTime)

        val dateFormatter: DateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm a", Locale.getDefault())
        noteDateTimeText.text = dateFormatter.format(Date())

        saveNoteBtn.setOnClickListener { saveNote() }

        val intent = intent
        if(intent.hasExtra(EXTRA_ID))
        {
            addActivityTitleText.text = "Edit Note"

            noteTitleText.setText(intent.getStringExtra(EXTRA_TITLE))
            noteSubtitleText.setText(intent.getStringExtra(EXTRA_SUBTITLE))
            noteDescriptionText.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            noteDateTimeText.text = dateFormatter.format(Date())
        }
        else
        {
            addActivityTitleText.text = "Create a Note"
        }
    }

    private fun saveNote()
    {
        val title = noteTitleText.text.toString()
        val subtitle = noteSubtitleText.text.toString()
        val description = noteDescriptionText.text.toString()
        val dateTime = noteDateTimeText.text.toString()

        if(title.trim().isEmpty())
        {
            Toast.makeText(this, "A title is required", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_SUBTITLE, subtitle)
        data.putExtra(EXTRA_DESCRIPTION, description)
        data.putExtra(EXTRA_DATE_TIME, dateTime)

        val id = getIntent().getIntExtra(EXTRA_ID, -1)
        if(id != -1)
        {
            data.putExtra(EXTRA_ID, id)
        }

        setResult(RESULT_OK, data)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}