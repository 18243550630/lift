package com.example.lifeservicesassistant.ui.theme.otro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lifeservicesassistant.util.CommonTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DateCalculatorApp(onNavigateBack = { finish() })
        }
    }
}

@Composable
fun DateCalculatorApp(onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("计算日期差", "计算结束日期")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()  // 避免与状态栏重叠
    ) {
        // 顶部导航栏
        CommonTopBar(
            title = "日期计算器",
            onNavigateBack = onNavigateBack,
        )

        // 主内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // 支持滚动
        ) {
            // 标签栏
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)  // 增加顶部间距
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // 内容区
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)  // 水平边距
            ) {
                when (selectedTab) {
                    0 -> DateDifferenceCalculator()
                    1 -> EndDateCalculator()
                }
            }
        }
    }
}

@Composable
fun DateDifferenceCalculator() {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")



    Column(
        modifier = Modifier.fillMaxWidth().padding(44.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        DatePickerButton(

            label = "起始日期",
            date = startDate,
            onDateSelected = { startDate = it },
            showPicker = showStartPicker,
            onShowChange = { showStartPicker = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DatePickerButton(
            label = "结束日期",
            date = endDate,
            onDateSelected = { endDate = it },
            showPicker = showEndPicker,
            onShowChange = { showEndPicker = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        val daysDifference = ChronoUnit.DAYS.between(startDate, endDate)
        Text(
            text = "日期差: ${daysDifference} 天",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun EndDateCalculator() {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var daysOffset by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Column(
        modifier = Modifier.fillMaxWidth().padding(44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DatePickerButton(
            label = "起始日期",
            date = startDate,
            onDateSelected = { startDate = it },
            showPicker = showDatePicker,
            onShowChange = { showDatePicker = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = daysOffset,
            onValueChange = { daysOffset = it },
            label = { Text("间隔天数") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val resultDate = try {
            startDate.plusDays(daysOffset.toLong())
        } catch (e: Exception) {
            null
        }

        Text(
            text = resultDate?.let { "结果日期: ${it.format(formatter)}" } ?: "请输入有效天数",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun DatePickerButton(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    showPicker: Boolean,
    onShowChange: (Boolean) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Button(
        onClick = { onShowChange(true) },
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text("$label: ${date.format(formatter)}")
    }

    if (showPicker) {
        DatePickerDialog(
            onDismiss = { onShowChange(false) },
            onConfirm = { selectedDate ->
                onDateSelected(selectedDate)
                onShowChange(false)
            },
            initialDate = date
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    initialDate: LocalDate
) {
    var selectedDate by remember { mutableStateOf(initialDate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 创建并使用 DatePickerState
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate.toEpochDay() * 86400000)

                // 使用 DatePickerState 来获取和设置日期
                DatePicker(state = datePickerState)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        // 安全调用，确保 selectedDateMillis 不是 null
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = LocalDate.ofEpochDay(millis / 86400000)
                            onConfirm(selectedLocalDate)
                        }
                    }) {
                        Text("确定")
                    }
                }
            }
        }
    }
}


