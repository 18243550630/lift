package com.example.lifeservicesassistant.ui.theme.healthy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NutrientScreen(
    viewModel: NutrientViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    var keyword by remember { mutableStateOf("") }
    val nutrientInfo by viewModel.nutrientInfo.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // 顶部返回按钮和标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "营养成分查询",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 搜索输入框
        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("输入食物名称（如：苹果、牛奶）") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 查询按钮
        Button(
            onClick = {
                if (keyword.isNotBlank()) {
                    isLoading = true
                    errorMessage = null
                    viewModel.fetchNutrient(apiKey, keyword)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("查询")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 错误提示
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // 显示营养信息
        nutrientInfo?.let { item ->
            Card(
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 核心营养成分
                    Text("核心营养成分", fontWeight = FontWeight.Bold)
                    NutrientItem("热量", "${item.rl} kcal")
                    NutrientItem("蛋白质", "${item.dbz} g")
                    NutrientItem("脂肪", "${item.zf} g")
                    NutrientItem("碳水化合物", "${item.shhf} g")
                    NutrientItem("膳食纤维", "${item.ys} g")

                    Spacer(modifier = Modifier.height(12.dp))

                    // 矿物质
                    Text("矿物质", fontWeight = FontWeight.Bold)
                    NutrientItem("钠", "${item.la} mg")
                    NutrientItem("钙", "${item.gai} mg")
                    NutrientItem("铁", "${item.tei} mg")

                    // 可根据需要添加更多字段...
                }
            }
        } ?: run {
            if (!isLoading) {
                Text(
                    text = "暂无数据，请输入食物名称查询",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        // 加载指示器
        if (isLoading && nutrientInfo == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // 监听ViewModel状态变化
    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { state ->
            isLoading = state.isLoading
            errorMessage = state.errorMessage
        }
    }
}

@Composable
private fun NutrientItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.Medium)
    }
}