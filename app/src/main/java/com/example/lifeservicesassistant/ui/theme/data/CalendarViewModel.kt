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
import com.example.lifeservicesassistant.ui.theme.data.LocalDateDeserializer
import com.example.lifeservicesassistant.ui.theme.data.LocalDateSerializer
import com.example.lifeservicesassistant.ui.theme.data.NotificationReceiver
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarViewModel(context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("calendar_events", Context.MODE_PRIVATE)
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .registerTypeAdapter(Event::class.java, EventSerializer())
        .registerTypeAdapter(Event::class.java, EventDeserializer())
        .create()

    // 使用 Map<LocalDate, MutableList<Event>> 存储每个日期的多个事件
    private val _events = mutableStateOf<Map<LocalDate, MutableList<Event>>>(emptyMap())
    init {
        loadEvents()  // 初始化时加载事件
    }

    // 从 SharedPreferences 中加载事件
    private fun loadEvents() {
        val eventsJson = sharedPreferences.getString("events", null)
        if (eventsJson != null) {
            try {
                val type: Type = object : TypeToken<Map<LocalDate, MutableList<Event>>>() {}.type
                val events: Map<LocalDate, MutableList<Event>> = gson.fromJson(eventsJson, type)
                _events.value = events
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                sharedPreferences.edit().remove("events").apply()  // 如果解析失败，清空数据
            }
        }
    }

    // 保存事件
    fun saveEvent(event: Event) {
        _events.value = _events.value.toMutableMap().apply {
            // 获取该日期已有的事件列表，没有则创建新列表
            val eventsForDate = getOrDefault(event.startDate, mutableListOf())
            eventsForDate.add(event) // 添加到列表
            put(event.startDate, eventsForDate) // 更新映射
        }
        saveEvents()
        // 如果启用了提醒和闹钟提醒，设置闹钟
        if (event.isReminderEnabled && event.isAlarmEnabled) {
            setAlarm(event, context)
        }
    }

    // 设置闹钟提醒
    private fun setAlarm(event: Event, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("event_title", event.title)  // 传递事件标题作为通知内容
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 计算开始时间前5分钟
        val reminderTime = event.startDate.atTime(event.startTime).minus(5, ChronoUnit.MINUTES)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            reminderTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
            pendingIntent
        )
    }

    // 保存所有事件到 SharedPreferences
    private fun saveEvents() {
        try {
            val eventsJson = gson.toJson(_events.value)
            sharedPreferences.edit().putString("events", eventsJson).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 获取指定月份的事件
    fun getEvents(month: LocalDate): Map<LocalDate, Event> {
        return _events.value.filter { it.key.month == month.month && it.key.year == month.year }
            .flatMap { entry ->
                entry.value.map { event -> entry.key to event }
            }
            .toMap()
    }

    // 更新事件
    fun updateEvent(event: Event) {
        _events.value = _events.value.toMutableMap().apply {
            val eventsForDate = getOrDefault(event.startDate, mutableListOf())
            val index = eventsForDate.indexOfFirst { it.startTime == event.startTime }
            if (index >= 0) {
                eventsForDate[index] = event // 如果已有相同时间的事件，替换它
            } else {
                eventsForDate.add(event) // 如果没有找到相同时间的事件，添加新的事件
            }
            put(event.startDate, eventsForDate)
        }
        saveEvents() // 更新后保存
    }

    // 删除事件
    fun deleteEvent(event: Event) {
        _events.value = _events.value.toMutableMap().apply {
            val eventsForDate = get(event.startDate)
            eventsForDate?.remove(event)  // 从同一天的事件列表中删除该事件
            if (eventsForDate.isNullOrEmpty()) {
                remove(event.startDate) // 如果该日期下没有事件了，移除日期键
            } else {
                put(event.startDate, eventsForDate)
            }
        }
        saveEvents() // 删除后保存
    }
}



