package com.interview.notes.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.interview.notes.R

/**
 * Main {@link android.app.Activity} which displays a list of existing Notes.
 */
class MainActivity : AppCompatActivity() {

    /*
    *************************************************************************************************************************
        NOTE: BEFORE MAKING CHANGES HERE, MAKE SURE THAT YOU CHANGE THE PACKAGE OF MainActivity IN AndroidManifest.xml
    *************************************************************************************************************************
    */

    private lateinit var viewModel: MainViewModel

    private var notesStore: NotesStore? = null

    private lateinit var notesList: RecyclerView
    private lateinit var addNoteButton: Button

    private val notesAdapter: NotesAdapter = NotesAdapter()

    private fun fetchNote() {
        viewModel.fetchNotes()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, MainViewModel.MainViewModelFactory((application as MainApplication).getNotesStore()!!)).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        fetchNote()

        notesStore = (application as MainApplication).getNotesStore()

        notesList = findViewById(R.id.notes_list)
        addNoteButton = findViewById(R.id.btn_add_note)
        notesList.layoutManager = LinearLayoutManager(this)
        notesList.adapter = notesAdapter
        // this part can be extracted to an repository, expose the data to a viewmodel and have fragment/activity observe the viewmodel
//        notesStore?.let { notesAdapter.setNotes(it.getNotes()) }

        addNoteButton.setOnClickListener {
            val addNoteActivityIntent = Intent(this, AddNoteActivity::class.java)
            startActivity(addNoteActivityIntent)
        }

        viewModel.notes.observe(this) {
            notesAdapter.setNotes(it)
        }
    }

    // implement onActivityResult to directly obtain the added note instead of making another request
    override fun onResume() {
        super.onResume()
    //    fetchNote()
    }


    // should be in another file for modularity
    private class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

        private val notes: MutableList<Note> = ArrayList()

        // it will refresh the whole recycleviewer, which is fine for small data list

        // ideally, we should implement a diffUtil to tell the recyclerview what has been changed and only re-render the data changed
        fun setNotes(freshNotes: List<Note>) {
            notes.clear()
            notes.addAll(freshNotes)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))
        }

        // move logic out of onBindViewHolder
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            val noteTitleView = holder.itemView.findViewById<TextView>(R.id.note_title)
            holder.textView.text = notes[position].title
        }

        override fun getItemCount(): Int {
            return notes.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.note_title)
        }

    }
}