package com.example.lifeservicesassistant.ui.theme.play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.EmptyPlaceholder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HoroscopeScreen(
    viewModel: HoroscopeViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("星座运势") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 星座选择器
            ZodiacSignPicker(
                selectedSign = state.selectedSign,
                onSignSelected = { sign ->
                    viewModel.fetchHoroscope(apiKey, sign, selectedDate.toString())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日期选择器
            DatePicker(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    viewModel.fetchHoroscope(apiKey, state.selectedSign, date.toString())
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 内容区域
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!)
                state.horoscopeItems.isEmpty() -> EmptyPlaceholder()
                else -> HoroscopeContent(state.horoscopeItems)
            }
        }
    }
}

@Composable
private fun ZodiacSignPicker(
    selectedSign: String,
    onSignSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedSign)
            Icon(
                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            zodiacSigns.keys.forEach { sign ->
                DropdownMenuItem(onClick = {
                    onSignSelected(sign)
                    expanded = false
                }) {
                    Text(sign)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val date = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(date)
                        }
                        showDatePicker.value = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) {
                    Text("取消")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(
                state = datePickerState
            )
        }
    }

    OutlinedButton(
        onClick = { showDatePicker.value = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(selectedDate.toString())
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
    }
}

@Composable
private fun HoroscopeContent(items: List<HoroscopeItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            HoroscopeCard(item)
        }
    }
}

@Composable
private fun HoroscopeCard(item: HoroscopeItem) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.type,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.content,
                style = MaterialTheme.typography.body1,
                lineHeight = 24.sp
            )
        }
    }
}