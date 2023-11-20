package com.example.tugas_p12

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugas_p12.database.Note
import com.example.tugas_p12.database.NoteDao
import com.example.tugas_p12.database.NoteRoomDatabase
import com.example.tugas_p12.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mNoteDao: NoteDao
    private lateinit var noteAdapter: NoteAdapter

    companion object {
        const val FORM_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_DATE = "extra_date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val db = NoteRoomDatabase.getDatabase(this)
        mNoteDao = db!!.noteDao()!!

        val allNote: LiveData<List<Note>>? = mNoteDao?.allNotes
        allNote?.observe(this) { note ->
            note?.let { noteAdapter.setData(it) }
        }

        noteAdapter = NoteAdapter(emptyList(), object : NoteAdapter.OnEditClickListener {
            override fun onEditClick(note: Note) {
                val intent = Intent(this@MainActivity, FormActivity::class.java).apply {
                    putExtra(FormActivity.EXTRA_UPDATE_ID, note.id)
                    putExtra(EXTRA_TITLE, note.title)
                    putExtra(EXTRA_DESC, note.description)
                    putExtra(EXTRA_DATE, note.date)
                }
                startActivityForResult(intent, FORM_ACTIVITY_REQUEST_CODE)
            }

            override fun onDeleteClick(note: Note) {
                deleteNoteInBackground(note)
            }
        })

        binding.rvNote.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, FormActivity::class.java)
            startActivityForResult(intent, FORM_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(emptyList(), object : NoteAdapter.OnEditClickListener {
            override fun onEditClick(note: Note) {
                // This implementation is empty because it will be replaced later
            }

            override fun onDeleteClick(note: Note) {
                deleteNoteInBackground(note)
            }
        })
        binding.rvNote.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }

    private fun deleteNoteInBackground(note: Note) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mNoteDao.delete(note)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FORM_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        }
    }
}
