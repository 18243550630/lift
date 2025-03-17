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
fun CalendarScreen() {
    val context = LocalContext.current // 获取 Context
    val viewModel: CalendarViewModel = remember { CalendarViewModel(context) } // 通过 remember 创建 ViewModel 实例
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)
    val currentMonth = LocalDate.now().plusMonths((pagerState.currentPage - Int.MAX_VALUE / 2).toLong())
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var currentEvent by remember { mutableStateOf<Event?>(null) }

    // 获取当前月的事件
    val events = viewModel.getEvents(currentMonth)
    var eventsList by remember { mutableStateOf(events.values.toList()) }

    // 获取当前选中日期的事件
    val filteredEvents = if (selectedDate != null) {
        eventsList.filter { it.startDate == selectedDate }
    } else {
        eventsList
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
                            // 判断点击的日期是否是已经选中的日期
                            selectedDate = if (date == selectedDate) null else date
                        },
                        selectedDate = selectedDate
                    )
                }
            }

            Text("事件列表", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

            // 按照 selectedDate 来过滤事件
            val (futureEvents, pastEvents) = remember(filteredEvents) {
                filteredEvents.sortedBy { it.startDate }
                    .partition { !it.startDate.isBefore(LocalDate.now()) }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(futureEvents) { event ->
                    EventCard(
                        event = event,
                        onDelete = {
                            viewModel.deleteEvent(event)
                            eventsList = viewModel.getEvents(currentMonth).values.toList()
                        },
                        onEdit = {
                            currentEvent = event
                            showDialog = true
                        }
                    )
                }
                items(pastEvents) { event ->
                    EventCard(
                        event = event,
                        onDelete = {
                            viewModel.deleteEvent(event)
                            eventsList = viewModel.getEvents(currentMonth).values.toList()
                        },
                        onEdit = {
                            currentEvent = event
                            showDialog = true
                        },
                        isPast = true
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                showDialog = true
                currentEvent = Event(
                    title = "",
                    startDate = selectedDate ?: LocalDate.now(),
                    startTime = LocalTime.now(),
                    endDate = selectedDate ?: LocalDate.now(),
                    endTime = LocalTime.now(),
                    note = "",
                    isReminderEnabled = false,
                    isAlarmEnabled = false
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge)
        }

        if (showDialog) {
            EventEditDialog(
                event = currentEvent,
                onDismiss = { showDialog = false },
                onSave = { event ->
                    viewModel.updateEvent(event)
                    eventsList = viewModel.getEvents(currentMonth).values.toList()
                    showDialog = false
                }
            )
        }
    }
}



@Composable
fun WeekdayHeader() {
    val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        weekdays.forEach { day ->
            Text(text = day, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun AnimatedCalendarMonthView(
    month: LocalDate,
    events: Map<LocalDate, Event>,
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
    events: Map<LocalDate, Event>,
    onDateClick: (LocalDate) -> Unit,
    selectedDate: LocalDate? // 添加 selectedDate 作为参数
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
                event = events[date],
                onClick = { onDateClick(date) },
                isSelected = date == selectedDate, // 比较是否为选中的日期
                isEventDate = events.containsKey(date) // 判断该日期是否有事件
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    event: Event?,
    onClick: () -> Unit,
    isSelected: Boolean,
    isEventDate: Boolean // 添加是否有事件的标记
) {
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() }  // 点击时触发回调
            .padding(4.dp)
            .background(
                if (isSelected) Color.Gray else Color.Transparent, // 如果是选中的日期，背景变灰
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center // 使用 Alignment.Center 来居中
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isToday -> Color.Blue
                            isEventDate -> Color.Green // 只有有事件的日期才会标记
                            else -> Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isToday) Color.White else Color.Black,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
            event?.let {
                Text(text = it.title, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun EventEditDialog(
    event: Event?,
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var startDate by remember { mutableStateOf(event?.startDate ?: LocalDate.now()) }
    var startTime by remember { mutableStateOf(event?.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "00:00") }
    var endDate by remember { mutableStateOf(event?.endDate ?: LocalDate.now()) }
    var endTime by remember { mutableStateOf(event?.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "00:00") }
    var note by remember { mutableStateOf(event?.note ?: "") }
    var isReminderEnabled by remember { mutableStateOf(event?.isReminderEnabled ?: false) }
    var isAlarmEnabled by remember { mutableStateOf(event?.isAlarmEnabled ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑事件") },
        text = {
            Column {
                // 标题
                TextField(value = title, onValueChange = { title = it }, label = { Text("事件标题") })

                // 开始日期选择
                DatePickerField(value = startDate, onValueChange = { startDate = it }, label = "开始日期")

                // 开始时间选择
                TimePickerField(time = startTime, onTimeChange = { startTime = it }, label = "开始时间")

                // 结束日期选择
                DatePickerField(value = endDate, onValueChange = { endDate = it }, label = "结束日期")

                // 结束时间选择
                TimePickerField(time = endTime, onTimeChange = { endTime = it }, label = "结束时间")

                // 备注
                TextField(value = note, onValueChange = { note = it }, label = { Text("备注") })

                // 提醒选择
                Row {
                    Text("提醒", modifier = Modifier.align(Alignment.CenterVertically))
                    Checkbox(
                        checked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it }
                    )
                }

                // 闹钟提醒选择
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
                // 将时间字符串转换为 LocalTime 类型
                val startLocalTime = LocalTime.parse(startTime)
                val endLocalTime = LocalTime.parse(endTime)

                // 创建新的事件，保存闹钟提醒状态
                val newEvent = Event(title, startDate, startLocalTime, endDate, endLocalTime, note, isReminderEnabled, isAlarmEnabled)
                onSave(newEvent)
                onDismiss()
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
                onTimeChange("${selectedHour ?: ""}:${selectedMinute ?: ""}")
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
                onTimeChange("${selectedHour ?: ""}:${selectedMinute ?: ""}")
            },
            label = { Text("分钟") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(60.dp)
        )
    }
}



@Composable
fun EventCard(
    event: Event,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isPast: Boolean = false
) {
    val isUpcoming = event.startDate >= LocalDate.now() // 判断是否为未完成事件
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) MaterialTheme.colorScheme.surfaceVariant
            else if (isUpcoming) Color(0xFFD3F9D8) // 浅绿色背景
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(event.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${event.startDate} ${event.startTime} - ${event.endTime}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (event.note.isNotBlank()) {
                    Text(event.note, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}
