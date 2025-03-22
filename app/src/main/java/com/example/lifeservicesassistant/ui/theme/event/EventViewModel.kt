package com.example.lifeservicesassistant.ui.theme.event

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventViewModel(context: Context) : ViewModel() {
    private val eventDao = EventDatabase.getInstance(context).eventDao()
    private val repository = EventRepository(eventDao)

    val eventLists: StateFlow<List<EventList>> = repository.getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentListId = MutableStateFlow<Long?>(null)
    val currentListId: StateFlow<Long?> = _currentListId

    val eventItems: StateFlow<List<EventItem>> = _currentListId
        .filterNotNull()
        .flatMapLatest { repository.getItemsByListId(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addList(title: String, onListCreated: (Long) -> Unit) = viewModelScope.launch {
        val listId = repository.addList(EventList(title = title))
        onListCreated(listId) // ✅ 立即跳转到详情页
    }


    fun deleteList(eventList: EventList) = viewModelScope.launch {
        repository.deleteList(eventList)
    }

    fun addEvent(listId: Long, title: String) = viewModelScope.launch {
        repository.addEvent(EventItem(listId = listId, eventTitle = title))
    }

    fun toggleEventCompleted(eventItem: EventItem) = viewModelScope.launch {
        repository.updateEvent(eventItem.copy(isCompleted = !eventItem.isCompleted))
    }

    fun deleteEvent(eventItem: EventItem) = viewModelScope.launch {
        repository.deleteEvent(eventItem)
    }

    fun setCurrentListId(listId: Long) {
        _currentListId.value = listId
    }
}
