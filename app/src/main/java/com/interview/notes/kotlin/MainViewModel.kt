package com.interview.notes.kotlin

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val notesStore: NotesStore
): ViewModel() {

    sealed class ViewEffect {
        object AddNoteSuccess: ViewEffect()
    }

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffect: Flow<ViewEffect> = _viewEffect.receiveAsFlow()


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
               notesStore.saveNote(note).collect {
                   if (it is Result.Success<List<Note>>) {
                       _viewEffect.send(ViewEffect.AddNoteSuccess)
                   } else if (it is Result.Error) {
                       // do somthing
                   }
               }
            }

        }
    }
}