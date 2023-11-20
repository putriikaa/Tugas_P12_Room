package com.example.tugas_p12

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tugas_p12.database.Note
import com.example.tugas_p12.databinding.ListNoteBinding

class NoteAdapter(private var listNote: List<Note>, private val onEditClickListener: OnEditClickListener) :
    RecyclerView.Adapter<NoteAdapter.ItemNoteViewHolder>() {

    interface OnEditClickListener {
        fun onEditClick(note: Note)
        fun onDeleteClick(note: Note)
    }

    inner class ItemNoteViewHolder(private val binding: ListNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.iconDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClickListener.onDeleteClick(listNote[position])
                }
            }

            binding.iconEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClickListener.onEditClick(listNote[position])
                }
            }
        }

        fun bind(note: Note) {
            with(binding) {
                textTitle.text = note.title
                textDesc.text = note.description
                textTgl.text = note.date
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newNote: List<Note>) {
        listNote = newNote
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemNoteViewHolder {
        val binding = ListNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ItemNoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemNoteViewHolder, position: Int) {
        holder.bind(listNote[position])
    }

    override fun getItemCount(): Int = listNote.size
}
