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
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", src?.title)
        jsonObject.addProperty("startDate", src?.startDate.toString())
        jsonObject.addProperty("startTime", src?.startTime.toString())
        jsonObject.addProperty("endDate", src?.endDate.toString())
        jsonObject.addProperty("endTime", src?.endTime.toString())
        jsonObject.addProperty("note", src?.note)
        jsonObject.addProperty("isReminderEnabled", src?.isReminderEnabled)
        return jsonObject
    }
}

class EventDeserializer : JsonDeserializer<Event> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Event {
        val jsonObject = json?.asJsonObject
        val title = jsonObject?.get("title")?.asString ?: ""
        val startDate = LocalDate.parse(jsonObject?.get("startDate")?.asString ?: "")
        val startTime = LocalTime.parse(jsonObject?.get("startTime")?.asString ?: "00:00")
        val endDate = LocalDate.parse(jsonObject?.get("endDate")?.asString ?: "")
        val endTime = LocalTime.parse(jsonObject?.get("endTime")?.asString ?: "00:00")
        val note = jsonObject?.get("note")?.asString ?: ""
        val isReminderEnabled = jsonObject?.get("isReminderEnabled")?.asBoolean ?: false
        val isAlarmEnabled = jsonObject?.get("isReminderEnabled")?.asBoolean ?: false
        return Event(title, startDate, startTime, endDate, endTime, note, isReminderEnabled,isAlarmEnabled)
    }
}

