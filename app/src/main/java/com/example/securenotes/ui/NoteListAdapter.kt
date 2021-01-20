package com.example.securenotes.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.R
import com.example.securenotes.database.Note
import kotlinx.android.synthetic.main.recycler_item.view.*

class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>()
{
    private var noteList = emptyList<Note>()
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder
    {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,
        parent, false)

        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int)
    {
        val currentItem = noteList[position]

        holder.itemTitle.itemTitle.text = currentItem.title
        holder.itemSubtitle.itemSubtitle.text = currentItem.subtitle
        holder.itemDescription.itemDescription.text = currentItem.description
        holder.itemDateTime.itemDateTime.text = currentItem.dateTime
    }

    override fun getItemCount() = noteList.size

    fun setData(notes: List<Note>)
    {
        this.noteList = notes
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int): Note
    {
        return noteList[position]
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var itemTitle: TextView = itemView.itemTitle
        var itemSubtitle: TextView = itemView.itemSubtitle
        var itemDescription: TextView = itemView.itemDescription
        var itemDateTime: TextView = itemView.itemDateTime

        init { itemView.setOnClickListener(this) }

        override fun onClick(v: View?)
        {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION)
            {
                listener.onItemClick(noteList[position])
            }
        }
    }

    interface OnItemClickListener
    {
        fun onItemClick(note: Note)
    }

    fun setOnClickListener(listener: OnItemClickListener)
    {
        this.listener = listener
    }
}