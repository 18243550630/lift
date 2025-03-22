package com.example.lifeservicesassistant.ui.theme.event

import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    fun getAllLists(): Flow<List<EventList>> = eventDao.getAllLists()
    fun getItemsByListId(listId: Long): Flow<List<EventItem>> = eventDao.getItemsByListId(listId)

    suspend fun addList(eventList: EventList): Long = eventDao.addList(eventList)
    suspend fun deleteList(eventList: EventList) = eventDao.deleteList(eventList)

    suspend fun addEvent(eventItem: EventItem) = eventDao.addEvent(eventItem)
    suspend fun updateEvent(eventItem: EventItem) = eventDao.updateEvent(eventItem)
    suspend fun deleteEvent(eventItem: EventItem) = eventDao.deleteEvent(eventItem)
}
