package com.example.lifeservicesassistant.ui.theme.data

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// LocalDate序列化和反序列化
class LocalDateSerializer : JsonSerializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toString())
    }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        return LocalDate.parse(json?.asString, DateTimeFormatter.ISO_DATE)
    }
}

// Event的序列化和反序列化
class EventSerializer : JsonSerializer<Event> {
    override fun serialize(src: Event?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject().apply {
            addProperty("id", src?.id)
            addProperty("title", src?.title)
            addProperty("startDate", src?.startDate.toString())
            addProperty("startTime", src?.startTime.toString())
            addProperty("endDate", src?.endDate.toString())
            addProperty("endTime", src?.endTime.toString())
            addProperty("note", src?.note)
            addProperty("isReminderEnabled", src?.isReminderEnabled)
            addProperty("isAlarmEnabled", src?.isAlarmEnabled)
        }
        return jsonObject
    }
}

class EventDeserializer : JsonDeserializer<Event> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Event {
        val jsonObject = json?.asJsonObject
        return Event(
            id = jsonObject?.get("id")?.asLong ?: 0,
            title = jsonObject?.get("title")?.asString ?: "",
            startDate = LocalDate.parse(jsonObject?.get("startDate")?.asString),
            startTime = LocalTime.parse(jsonObject?.get("startTime")?.asString),
            endDate = LocalDate.parse(jsonObject?.get("endDate")?.asString),
            endTime = LocalTime.parse(jsonObject?.get("endTime")?.asString),
            note = jsonObject?.get("note")?.asString ?: "",
            isReminderEnabled = jsonObject?.get("isReminderEnabled")?.asBoolean ?: false,
            isAlarmEnabled = jsonObject?.get("isAlarmEnabled")?.asBoolean ?: false
        )
    }
}
