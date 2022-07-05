package com.interview.notes.kotlin

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val notesStore: NotesStore
): ViewModel() {

    class MainViewModelFactory(val notesStore: NotesStore): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(notesStore) as T
        }

    }

    private val _notes: MutableLiveData<List<Note>> = MutableLiveData(listOf())
    val notes: LiveData<List<Note>> = _notes

    fun fetchNotes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               val listNode = notesStore.getNotes()
               _notes.postValue(listNode)
            }
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               notesStore.saveNote(note)
            }
        }
    }
}