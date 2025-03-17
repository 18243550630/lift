package com.example.lifeservicesassistant.ui.theme.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


data class Event(
    val title: String,
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime,
    val note: String,
    val isReminderEnabled: Boolean,
    val isAlarmEnabled: Boolean // 新增闹钟提醒字段
)

