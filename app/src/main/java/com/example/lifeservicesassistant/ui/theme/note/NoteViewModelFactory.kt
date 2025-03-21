package com.example.lifeservicesassistant.ui.theme.note

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifeservicesassistant.ui.theme.note.NoteRepository
import com.example.lifeservicesassistant.ui.theme.note.NoteViewModel

class NoteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    private val noteDao = NoteDatabase.getInstance(context).noteDao()
    private val repository = NoteRepository(noteDao)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(context, repository) as T  // ✅ 传递 context
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
