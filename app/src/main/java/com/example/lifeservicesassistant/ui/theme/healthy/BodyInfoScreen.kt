package com.example.lifeservicesassistant.ui.theme.healthy

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeservicesassistant.logic.dao.UserPreferences
import com.example.lifeservicesassistant.util.CommonTopBar

@Composable
fun BodyInfoScreen(onNavigateBack: () -> Unit) {
    // 从 SharedPreferences 获取数据
    val context = LocalContext.current
    val userInfo = remember { UserPreferences.getUserInfo(context) }

    // 初始数据从 SharedPreferences 加载
    var age by remember { mutableStateOf(userInfo.age) }
    var height by remember { mutableStateOf(userInfo.height) } // 身高
    var weight by remember { mutableStateOf(userInfo.weight) } // 体重
    var bloodPressure by remember { mutableStateOf(userInfo.bloodPressure) } // 血压
    var bodyFat by remember { mutableStateOf(userInfo.bodyFat) } // 体脂率

    // 用来弹出对话框的控制变量
    var showDialog by remember { mutableStateOf(false) }

    // 更新数据的函数
    fun updateField(field: String, value: String) {
        when (field) {
            "age" -> age = value.toIntOrNull() ?: 0
            "height" -> height = value.toFloatOrNull() ?: 0f  // 确保是Float类型
            "weight" -> weight = value.toFloatOrNull() ?: 0f  // 确保是Float类型
            "bloodPressure" -> bloodPressure = value.toIntOrNull() ?: 0
            "bodyFat" -> bodyFat = value.toFloatOrNull() ?: 0f  // 确保是Float类型
        }
        // 数据更新后保存到 SharedPreferences
        UserPreferences.saveUserInfo(context, age, height, weight, bloodPressure, bodyFat)
    }

    // 计算BMI
    fun calculateBMI(): String {
        return if (weight > 0 && height > 0) {
            val bmi = weight / (height * height)  // 确保height是以米为单位（例如：1.75米）
            "BMI: %.2f".format(bmi)
        } else {
            "请输入有效的身高和体重"
        }
    }


    // 关闭对话框
    fun closeDialog() {
        showDialog = false
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "身体信息",
                onNavigateBack = onNavigateBack // 传入返回操作
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 显示各项信息
                InfoRow("年龄", age.toString(), "age")
                InfoRow("身高 (m)", height.toString(), "height")
                InfoRow("体重 (kg)", weight.toString(), "weight")
                InfoRow("血压", bloodPressure.toString(), "bloodPressure")
                InfoRow("体脂率", bodyFat.toString(), "bodyFat")

                // 显示计算出的BMI
                Text(
                    text = calculateBMI(),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // 加号按钮，点击后弹出对话框
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)  // 这里使用对的 Alignment 类型
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Info")
            }
        }

        // 输入数据的对话框
        if (showDialog) {
            InputFormDialog(
                onConfirm = { field, value ->
                    updateField(field, value)
                    closeDialog() // 关闭对话框
                },
                onDismiss = { closeDialog() }
            )
        }
    }
}

// 显示每个数据项的行
@Composable
fun InfoRow(label: String, value: String, field: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // 改为使用 Arrangement
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))  // 使用 TextStyle
        Text(value, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light))  // 使用 TextStyle
    }
}

// 用来显示弹出输入框的对话框
@Composable
fun InputFormDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var ageInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var bloodPressureInput by remember { mutableStateOf("") }
    var bodyFatInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "请输入身体信息", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) },
        text = {
            Column {
                TextField(
                    value = ageInput,
                    onValueChange = { ageInput = it },
                    label = { Text("年龄") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("身高 (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("体重 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = bloodPressureInput,
                    onValueChange = { bloodPressureInput = it },
                    label = { Text("血压") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = bodyFatInput,
                    onValueChange = { bodyFatInput = it },
                    label = { Text("体脂率") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm("age", ageInput)
                    onConfirm("height", heightInput)
                    onConfirm("weight", weightInput)
                    onConfirm("bloodPressure", bloodPressureInput)
                    onConfirm("bodyFat", bodyFatInput)
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

