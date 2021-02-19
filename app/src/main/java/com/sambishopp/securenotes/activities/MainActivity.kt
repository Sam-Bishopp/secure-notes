package com.sambishopp.securenotes.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sambishopp.securenotes.R
import com.sambishopp.securenotes.database.Note
import com.sambishopp.securenotes.ui.NoteListAdapter
import com.sambishopp.securenotes.ui.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object
    {
        private const val ADD_NOTE_REQUEST: Int = 1
        private const val EDIT_NOTE_REQUEST: Int = 2
    }

    private val checkUserActivityTime: Long = 540000 //5000
    private lateinit var checkUserActivityTimer: CountDownTimer

    private lateinit var logoutAlertDialog: AlertDialog
    private lateinit var deleteNoteAlertDialog: AlertDialog

    private lateinit var lockTimer: CountDownTimer
    private val appLockTime: Long = 600000 //10000 //5000

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorCardView)
        setContentView(R.layout.activity_main)

        checkUserActivity()
        startUserSession()

        val noteSearchEditText = findViewById<EditText>(R.id.noteSearch)
        noteSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    searchDatabase(p0)
                }
            }
        })

        val clearSearchImage = findViewById<ImageView>(R.id.clearSearch)
        clearSearchImage.setOnClickListener {
            noteSearchEditText.setText("")
        }

        val buttonAddNote = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        buttonAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        adapter = NoteListAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.notesRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        noteViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                this.application
            )
        ).get(NoteViewModel::class.java)

        getAllNotesDisplay()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val notePosition = adapter.getNoteAt(viewHolder.adapterPosition)
                deleteNoteDialog(notePosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        adapter.setOnClickListener(object : NoteListAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)

                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(AddEditNoteActivity.EXTRA_SUBTITLE, note.subtitle)
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.description)
                intent.putExtra(AddEditNoteActivity.EXTRA_DATE_TIME, note.dateTime)
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.id)

                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }
        })
    }

    fun searchDatabase(query: Editable?) {
        val searchQuery = "%$query%"
        noteViewModel.searchNotes(searchQuery)?.observe(this, { searchedNotes ->
            searchedNotes.let {
                adapter.setData(
                    searchedNotes
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            val title = data?.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val subtitle = data?.getStringExtra(AddEditNoteActivity.EXTRA_SUBTITLE)
            val description = data?.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val dateTime = data?.getStringExtra(AddEditNoteActivity.EXTRA_DATE_TIME)

            val note = Note(title, subtitle, description, dateTime, 0)
            noteViewModel.insertNote(note)

            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1)
            val title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val subtitle = data.getStringExtra(AddEditNoteActivity.EXTRA_SUBTITLE)
            val description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val dateTime = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE_TIME)

            val note = Note(title, subtitle, description, dateTime, id)
            noteViewModel.updateNote(note)

            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAllNotesDisplay() {
        noteViewModel.getAllNotes()?.observe(this, Observer { notes ->
            notes.let {
                adapter.setData(
                    notes
                )
            }
        })

        if(this::deleteNoteAlertDialog.isInitialized)
        {
            deleteNoteAlertDialog.dismiss()
        }
    }

    private fun deleteNote(notePosition: Note) {
        noteViewModel.deleteNote(notePosition)
        Toast.makeText(this@MainActivity, "Note Deleted", Toast.LENGTH_SHORT).show()
    }

    fun deleteNoteDialog(notePosition: Note) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Note")
        builder.setMessage("Are you sure you want to delete this note?")

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ -> deleteNote(notePosition) })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->  getAllNotesDisplay()})

        deleteNoteAlertDialog = builder.create()
        deleteNoteAlertDialog.show()
    }

    private fun checkUserActivity() {
        checkUserActivityTimer = object : CountDownTimer(checkUserActivityTime, 1000) {
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                checkUserActivityDialog()
            }

        }.start()
    }

    private fun userStillActive() {
        checkUserActivityTimer.cancel()
        lockTimer.cancel()

        checkUserActivity()
        startUserSession()
    }

    fun checkUserActivityDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Auto Logout in 1 minute")
        builder.setMessage("Do you wish to continue using the app?")

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ -> userStillActive() })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> logoutUser() })

        logoutAlertDialog = builder.create()
        logoutAlertDialog.show()
    }

    private fun startUserSession() {
        lockTimer = object : CountDownTimer(appLockTime, 1000) {
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                logoutUser()
            }

        }.start()
    }

    fun logoutUser() {
        val logoutIntent = Intent(this, LoginActivity::class.java)
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        lockTimer.cancel()
        logoutAlertDialog.dismiss()
        if(this::deleteNoteAlertDialog.isInitialized) { deleteNoteAlertDialog.dismiss() }
        startActivity(logoutIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        var menuSelected = ""

        when(id)
        {
            R.id.settings_menu -> menuSelected = "Settings Menu"
        }
        Toast.makeText(this, menuSelected, Toast.LENGTH_SHORT).show()

        if(id == R.id.settings_menu)
        {
            val intent = Intent(this, PreferenceActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}