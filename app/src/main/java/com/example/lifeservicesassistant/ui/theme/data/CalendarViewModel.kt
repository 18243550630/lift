import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifeservicesassistant.SunnyWeatherApplication.Companion.context
import com.example.lifeservicesassistant.ui.theme.data.Event
import com.example.lifeservicesassistant.ui.theme.data.EventDeserializer
import com.example.lifeservicesassistant.ui.theme.data.EventSerializer
import com.example.lifeservicesassistant.ui.theme.data.NotificationReceiver
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.example.lifeservicesassistant.ui.theme.data.LocalDateDeserializer
import com.example.lifeservicesassistant.ui.theme.data.LocalDateSerializer
class CalendarViewModel(context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("calendar_events", Context.MODE_PRIVATE)
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .registerTypeAdapter(Event::class.java, EventSerializer())
        .registerTypeAdapter(Event::class.java, EventDeserializer())
        .create()

    // 修改为 Map<LocalDate, MutableList<Event>> 支持多事件
    private val _events = mutableStateOf<Map<LocalDate, MutableList<Event>>>(emptyMap())

    init {
        loadEvents()
    }
    // CalendarViewModel.kt
/*    fun clearAllEvents() {
        // 1. 清空内存中的事件数据，并确保 Compose 重新渲染
        _events.value = mutableMapOf()

        // 2. 清空本地存储数据
        sharedPreferences.edit().remove("events").apply()

    }*/

    private fun loadEvents() {
        val eventsJson = sharedPreferences.getString("events", null)
        try {
            val type = object : TypeToken<Map<LocalDate, MutableList<Event>>>() {}.type
            val loadedEvents: Map<LocalDate, MutableList<Event>> = gson.fromJson(eventsJson, type)

            // 确保每个事件的 ID 唯一
            _events.value = loadedEvents.mapValues { entry ->
                entry.value.map { event ->
                    if (event.id == 0L) event.copy(id = System.currentTimeMillis() + System.nanoTime())
                    else event
                }.toMutableList()
            }
            _events.value = HashMap(_events.value)

        } catch (e: Exception) {
            _events.value = mutableMapOf()
        }
    }


/*
    fun saveEvent(event: Event) {
        _events.value = _events.value.toMutableMap().apply {
            val eventsForDate = getOrDefault(event.startDate, mutableListOf()).apply {
                add(event.copy(id = System.currentTimeMillis())) // 生成唯一ID
            }
            put(event.startDate, eventsForDate.sortedBy { it.startTime }.toMutableList())
        }
        saveEvents()
        setAlarmIfNeeded(event, context)
    }
*/
// CalendarViewModel.kt
fun saveEvent(event: Event) {
    val finalEvent = if (event.id == 0L) {
        event.copy(id = System.currentTimeMillis())
    } else {
        event
    }

    _events.value = _events.value.toMutableMap().apply {
        val eventsForDate = getOrDefault(finalEvent.startDate, mutableListOf())
        eventsForDate.add(finalEvent)
        put(finalEvent.startDate, eventsForDate.sortedBy { it.startTime }.toMutableList())
    }
    saveEvents()
}

    fun getEvents(month: LocalDate): Map<LocalDate, List<Event>> {
        return _events.value
            .filterKeys { it.month == month.month && it.year == month.year }
            .mapValues { it.value.sortedBy { event -> event.startTime } }
    }

    fun updateEvent(updatedEvent: Event) {
        _events.value = _events.value.toMutableMap().apply {
            val eventsForDate = getOrDefault(updatedEvent.startDate, mutableListOf())
            val index = eventsForDate.indexOfFirst { it.id == updatedEvent.id }
            if (index != -1) {
                eventsForDate[index] = updatedEvent
                put(updatedEvent.startDate, eventsForDate.sortedBy { it.startTime }.toMutableList())
            }
        }
        saveEvents()
        setAlarmIfNeeded(updatedEvent, context)
    }

    fun deleteEvent(event: Event) {
        _events.value = _events.value.toMutableMap().apply {
            val eventsForDate = get(event.startDate)
            eventsForDate?.removeAll { it.id == event.id }
            if (eventsForDate.isNullOrEmpty()) {
                remove(event.startDate)
            } else {
                put(event.startDate, eventsForDate)
            }
        }
        saveEvents()
        cancelAlarm(event, context)
    }

    private fun saveEvents() {
        sharedPreferences.edit().putString("events", gson.toJson(_events.value)).apply()
    }

    private fun setAlarmIfNeeded(event: Event, context: Context) {
        if (event.isReminderEnabled && event.isAlarmEnabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("event_id", event.id)
                putExtra("event_title", event.title)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                event.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val triggerAtMillis = event.startDate.atTime(event.startTime)
                .minus(5, ChronoUnit.MINUTES)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm(event: Event, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

