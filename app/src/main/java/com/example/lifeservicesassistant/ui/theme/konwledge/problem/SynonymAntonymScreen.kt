package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.healthy.LoadingIndicator

@Composable
fun SynonymAntonymScreen(
    viewModel: SynonymAntonymViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    var keyword by remember { mutableStateOf("") }
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // 顶部栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "返回")
            }
            Text("词语近反义词", style = MaterialTheme.typography.h5)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 搜索栏
        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("输入词语（如：一帆风顺）") },
            trailingIcon = {
                if (keyword.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.search(apiKey, keyword) },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Search, "搜索")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(onSearch = { viewModel.search(apiKey, keyword) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 错误提示
        error?.let { message ->
            ErrorMessage(message)
        }

        // 结果展示
        when {
            isLoading -> LoadingIndicator()
            result != null -> SynonymAntonymResultCard(result!!)
            else -> SyEmptyPlaceholder()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun SynonymAntonymResultCard(result: SynonymAntonymResult) {
    val synonyms = remember(result.jyc) {
        result.jyc.split(",").filter { it.isNotBlank() }
    }
    val antonyms = remember(result.fyc) {
        result.fyc.split(",").filter { it.isNotBlank() }
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "查询词：${result.words}",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp))


            Divider()

            // 近义词部分
            Text(
                text = "近义词",
                style = MaterialTheme.typography.subtitle1,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(vertical = 8.dp))

            if (synonyms.isEmpty()) {
                Text("暂无近义词数据", style = MaterialTheme.typography.body2)
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    synonyms.forEach { word ->
                        Chip(
                            onClick = { /* 可以添加点击查询功能 */ },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = Color(0xFFE8F5E9)
                            )
                        ) {
                            Text(word)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 反义词部分
            Text(
                text = "反义词",
                style = MaterialTheme.typography.subtitle1,
                color = Color(0xFFF44336),
                modifier = Modifier.padding(vertical = 8.dp))

            if (antonyms.isEmpty()) {
                Text("暂无反义词数据", style = MaterialTheme.typography.body2)
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    antonyms.forEach { word ->
                        Chip(
                            onClick = { /* 可以添加点击查询功能 */ },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Text(word)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyEmptyPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.HistoryEdu,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text("输入词语查询近反义词")
        }
    }
}