package com.example.lifeservicesassistant.ui.theme.event

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class EventList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String
)

@Entity
data class EventItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long, // 所属清单ID
    val eventTitle: String,
    val isCompleted: Boolean = false
)

@Dao
interface EventDao {
    @Query("SELECT * FROM EventList ORDER BY id DESC")
    fun getAllLists(): Flow<List<EventList>>

    @Insert
    suspend fun addList(eventList: EventList): Long

    @Delete
    suspend fun deleteList(eventList: EventList)

    @Query("SELECT * FROM EventItem WHERE listId = :listId ORDER BY id DESC")
    fun getItemsByListId(listId: Long): Flow<List<EventItem>>

    @Insert
    suspend fun addEvent(eventItem: EventItem)

    @Update
    suspend fun updateEvent(eventItem: EventItem)

    @Delete
    suspend fun deleteEvent(eventItem: EventItem)
}

@Database(entities = [EventList::class, EventItem::class], version = 1)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var instance: EventDatabase? = null
        fun getInstance(context: Context): EventDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).build().also { instance = it }
            }
        }
    }
}
