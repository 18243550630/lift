package com.example.lifeservicesassistant.ui.theme.data

import CalendarViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = remember { CalendarViewModel(context) }
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)
    val currentMonth = LocalDate.now().plusMonths((pagerState.currentPage - Int.MAX_VALUE / 2).toLong())

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var currentEvent by remember { mutableStateOf<Event?>(null) }

    var refreshFlag by remember { mutableStateOf(0) }

    // 获取当前月的事件（支持多事件）
    val events by remember(currentMonth) {
        derivedStateOf { viewModel.getEvents(currentMonth) }
    }

    // 展开所有事件并按时间排序
    val allEvents by remember(events) {
        derivedStateOf {
            events.values.flatten()
                .sortedWith(
                    compareByDescending<Event> { it.startDate }
                        .thenBy { it.startTime }
                )
        }
    }

    // 筛选选中日期的事件
    val filteredEvents by remember(selectedDate, allEvents) {
        derivedStateOf {
            selectedDate?.let { date ->
                allEvents.filter { it.startDate == date }
            } ?: allEvents
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "${currentMonth.year}年 ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                HorizontalPager(state = pagerState, count = Int.MAX_VALUE) { page ->
                    val month = LocalDate.now().plusMonths((page - Int.MAX_VALUE / 2).toLong())
                    AnimatedCalendarMonthView(
                        month = month,
                        events = events,
                        onDateClick = { date ->
                            selectedDate = if (date == selectedDate) null else date
                        },
                        selectedDate = selectedDate
                    )
                }
            }

            Text(
                "事件列表",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (filteredEvents.isEmpty()) {
                    item {
                        Text(
                            "没有找到事件",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                items(
                    items = filteredEvents,
                    key = { event -> "${event.id}-$refreshFlag" }
                ) { event ->
                    EventCard(
                        event = event,
                        onDelete = {
                            viewModel.deleteEvent(event)
                            refreshFlag++ // 触发列表刷新
                        },
                        onEdit = {
                            currentEvent = event
                            showDialog = true
                        },
                        isPast = event.startDate.isBefore(LocalDate.now())
                    )
                }
            }
        }

        // 添加事件按钮（修正位置）
        FloatingActionButton(
            onClick = {
                showDialog = true
                currentEvent = null
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // 添加间距
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge)
        }

        if (showDialog) {
            EventEditDialog(
                event = currentEvent,
                selectedDate = selectedDate, // 传递选中日期
                onDismiss = { showDialog = false },
                onSave = { updatedEvent ->
                    if (currentEvent == null) {
                        viewModel.saveEvent(updatedEvent)
                    } else {
                        viewModel.updateEvent(updatedEvent)
                    }
                    // 强制刷新数据
                    //events = viewModel.getEvents(currentMonth)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AnimatedCalendarMonthView(
    month: LocalDate,
    events: Map<LocalDate, List<Event>>,
    onDateClick: (LocalDate) -> Unit,
    selectedDate: LocalDate?
) {
    var visibleState by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = visibleState) {
        Column {
            WeekdayHeader()
            CalendarMonthView(month, events, onDateClick, selectedDate)
        }
    }

    LaunchedEffect(month) {
        visibleState = false
        delay(100)
        visibleState = true
    }
}

@Composable
fun CalendarMonthView(
    month: LocalDate,
    events: Map<LocalDate, List<Event>>,
    onDateClick: (LocalDate) -> Unit,
    selectedDate: LocalDate?
) {
    val daysInMonth = month.lengthOfMonth()
    val firstDay = month.withDayOfMonth(1)
    val startOffset = firstDay.dayOfWeek.value % 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize()
    ) {
        items(startOffset) { Spacer(modifier = Modifier) }

        items(daysInMonth) { day ->
            val date = firstDay.plusDays(day.toLong())
            CalendarDayCell(
                date = date,
                eventCount = events[date]?.size ?: 0, // 显示事件数量
                onClick = { onDateClick(date) },
                isSelected = date == selectedDate,
                hasEvents = events.containsKey(date)
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    eventCount: Int,
    onClick: () -> Unit,
    isSelected: Boolean,
    hasEvents: Boolean
) {
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .padding(4.dp)
            .background(
                color = when {
                    isSelected -> Color.LightGray
                    isToday -> Color.Blue.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isToday -> Color.White
                    isSelected -> Color.DarkGray
                    else -> Color.Black
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )

            // 显示事件数量
            if (eventCount > 0) {
                Text(
                    text = "$eventCount 事件",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isPast: Boolean
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPast -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                Text(
                    text = "${event.startDate} ${event.startTime.format(timeFormatter)} - " +
                            "${event.endTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (event.note.isNotBlank()) {
                    Text(
                        text = event.note,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.isReminderEnabled) {
                        Text(
                            "⏰ 已设置提醒",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}

@Composable
fun WeekdayHeader() {
    val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        weekdays.forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun EventEditDialog(
    event: Event?,
    selectedDate: LocalDate?, // 新增参数
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit
) {
    val initialId = remember { if (event == null) System.currentTimeMillis() else event.id }
    var id by remember { mutableStateOf(initialId) }

    // 使用选中日期作为默认值
    var startDate by remember {
        mutableStateOf(event?.startDate ?: selectedDate ?: LocalDate.now())
    }
    var endDate by remember {
        mutableStateOf(event?.endDate ?: selectedDate ?: LocalDate.now())
    }

    var title by remember { mutableStateOf(event?.title ?: "") }
    var startTime by remember { mutableStateOf(event?.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "00:00") }
    var endTime by remember { mutableStateOf(event?.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "00:00") }
    var note by remember { mutableStateOf(event?.note ?: "") }
    var isReminderEnabled by remember { mutableStateOf(event?.isReminderEnabled ?: false) }
    var isAlarmEnabled by remember { mutableStateOf(event?.isAlarmEnabled ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑事件") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("事件标题") })

                DatePickerField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = "开始日期"
                )

                TimePickerField(
                    time = startTime,
                    onTimeChange = { startTime = it },
                    label = "开始时间"
                )

                DatePickerField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = "结束日期"
                )

                TimePickerField(
                    time = endTime,
                    onTimeChange = { endTime = it },
                    label = "结束时间"
                )

                TextField(value = note, onValueChange = { note = it }, label = { Text("备注") })

                Row {
                    Text("提醒", modifier = Modifier.align(Alignment.CenterVertically))
                    Checkbox(
                        checked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it }
                    )
                }

                Row {
                    Text("闹钟提醒", modifier = Modifier.align(Alignment.CenterVertically))
                    Checkbox(
                        checked = isAlarmEnabled,
                        onCheckedChange = { isAlarmEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    // 确保时间格式始终为两位数（HH:mm）
                    val formattedStartTime = if (startTime.length == 4) "0$startTime" else startTime
                    val formattedEndTime = if (endTime.length == 4) "0$endTime" else endTime

                    val newEvent = Event(
                        id = id,
                        title = title,
                        startDate = startDate,
                        startTime = LocalTime.parse(formattedStartTime, DateTimeFormatter.ofPattern("HH:mm")),
                        endDate = endDate,
                        endTime = LocalTime.parse(formattedEndTime, DateTimeFormatter.ofPattern("HH:mm")),
                        note = note,
                        isReminderEnabled = isReminderEnabled,
                        isAlarmEnabled = isAlarmEnabled
                    )

                    onSave(newEvent)
                    onDismiss()
                } catch (e: Exception) {
                    println("时间格式错误: ${e.message}")
                }
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}


@Composable
fun DatePickerField(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    label: String
) {
    val formattedDate = remember(value) { value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    TextField(
        value = formattedDate,
        onValueChange = { newText ->
            val newDate = try {
                LocalDate.parse(newText, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (e: Exception) {
                null
            }
            if (newDate != null) onValueChange(newDate)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun TimePickerField(
    time: String,
    onTimeChange: (String) -> Unit,
    label: String
) {
    var selectedHour by remember { mutableStateOf(time.split(":").getOrNull(0)?.toIntOrNull()) }
    var selectedMinute by remember { mutableStateOf(time.split(":").getOrNull(1)?.toIntOrNull()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 小时选择
        TextField(
            value = selectedHour?.toString() ?: "",  // 如果小时为空则显示为空字符串
            onValueChange = { newHour ->
                if (newHour.isEmpty()) {
                    selectedHour = null  // 删除后清空
                } else {
                    selectedHour = newHour.toIntOrNull()?.coerceIn(0, 23)
                }
                // 更新时间
                onTimeChange(
                    "%02d:%02d".format(selectedHour ?: 0, selectedMinute ?: 0)
                )
            },
            label = { Text("小时") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(60.dp)
        )

        Text(":")

        // 分钟选择
        TextField(
            value = selectedMinute?.toString() ?: "",  // 如果分钟为空则显示为空字符串
            onValueChange = { newMinute ->
                if (newMinute.isEmpty()) {
                    selectedMinute = null  // 删除后清空
                } else {
                    selectedMinute = newMinute.toIntOrNull()?.coerceIn(0, 59)
                }
                // 更新时间
                onTimeChange(
                    "%02d:%02d".format(selectedHour ?: 0, selectedMinute ?: 0)
                )
            },
            label = { Text("分钟") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(60.dp)
        )
    }
}


