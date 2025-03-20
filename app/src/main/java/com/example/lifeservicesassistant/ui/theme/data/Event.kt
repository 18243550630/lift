package com.example.lifeservicesassistant.ui.theme.data

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Long = System.currentTimeMillis(), // 添加唯一标识符
    val title: String,
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime,
    val note: String,
    val isReminderEnabled: Boolean,
    val isAlarmEnabled: Boolean
)

