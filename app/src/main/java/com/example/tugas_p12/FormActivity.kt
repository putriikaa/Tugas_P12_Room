package com.example.tugas_p12

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.tugas_p12.database.Note
import com.example.tugas_p12.database.NoteDao
import com.example.tugas_p12.database.NoteRoomDatabase
import com.example.tugas_p12.databinding.ActivityFormBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var mNoteDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0

    companion object {
        const val EXTRA_UPDATE_ID = "extra_update_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_DATE = "extra_date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        if (db != null) {
            mNoteDao = db.noteDao()!!
        }

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateId = intent.getIntExtra(EXTRA_UPDATE_ID, 0)
        if (updateId > 0) {
            // If ID is greater than 0, it's an update mode
            val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
            val description = intent.getStringExtra(EXTRA_DESC) ?: ""
            val date = intent.getStringExtra(EXTRA_DATE) ?: ""

            with(binding) {
                txtTitle.setText(title)
                txtDescription.setText(description)
                txtDate.setText(date)
            }
        }

        with(binding) {
            btnAdd.setOnClickListener {
                if (txtTitle.text.isNotEmpty() && txtDescription.text.isNotEmpty() && txtDate.text.isNotEmpty()) {
                    val note = Note(
                        title = txtTitle.text.toString(),
                        description = txtDescription.text.toString(),
                        date = txtDate.text.toString()
                    )
                    insert(note)
                    resetForm()
                    setResultAndFinish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please fill the form correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnUpdate.setOnClickListener {
                // In this case, updateId should already be set
                val note = Note(
                    id = updateId,
                    title = txtTitle.text.toString(),
                    description = txtDescription.text.toString(),
                    date = txtDate.text.toString()
                )
                update(note)
                setResultAndFinish()
            }
            back.setOnClickListener {
                val intent= Intent(this@FormActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNoteDao.insert(note) }
    }

    private fun update(note: Note) {
        executorService.execute { mNoteDao.update(note) }
    }

    private fun delete(note: Note) {
        executorService.execute { mNoteDao.delete(note) }
    }

    private fun resetForm() {
        with(binding) {
            txtTitle.setText("")
            txtDate.setText("")
            txtDescription.setText("")
        }
    }

    private fun setResultAndFinish() {
        val resultIntent = Intent().apply {
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }
}
