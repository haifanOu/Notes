package com.interview.notes.kotlin

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

sealed <out T: Any> class Result {
    class Success<T>(val date: T): Result<T>()
    class Error(val msg: String): Result<Nothing>()
}

class NotesStoreImpl(applicationContext: Context) : NotesStore {

    private val NOTES_KEY = "notes_key"

    private val preferences = applicationContext.getSharedPreferences("NotePref", Context.MODE_PRIVATE)
    private val editor = preferences.edit()
    private val gson: Gson = Gson()

    override fun getNotes(): MutableList<Note> {
        val notesJson = preferences.getString(NOTES_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<MutableList<Note>>() {}.type
        return gson.fromJson(notesJson, type)
    }


    override suspend fun saveNote(note: Note): Flow<List<Note>> = flow {
        val notes = getNotes()
        notes.add(note)

        try {
            // adding the note into local storage
            val jsonNotes = gson.toJson(notes)
            editor.putString(NOTES_KEY, jsonNotes)
            editor.apply()
            emit(notes)
        } catch (e: Exception) {
            notes.remove(note)
            emit(notes)
        }
        // make an network request
        // if the network succeeds, expose the same data in local cache
        // if the network request fails, we can remove the added note and expose the old list
    }
}