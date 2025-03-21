package com.example.lifeservicesassistant.ui.theme.note

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
    
    suspend fun addNote(note: Note) = noteDao.insertNote(note)
    
    suspend fun updateNote(note: Note) = noteDao.insertNote(note)
    
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
}