package com.example.second

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class NotesViewModel : ViewModel() {
    private val notes1 = mutableStateListOf<Note>()
    val notes: List<Note> get() = notes1

    fun addNote(note: Note) {
        if (!notes1.contains(note)) {
            notes1.add(note)
        }
    }
}
