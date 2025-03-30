package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MedicineInstructionScreen(
    viewModel: MedicineInstructionViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    var keyword by remember { mutableStateOf("") }
    val instructions by viewModel.instructions.collectAsState()
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
            Text("药品说明书查询", style = MaterialTheme.typography.h5)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 搜索栏
        MedicineSearchBar(
            keyword = keyword,
            onKeywordChange = { keyword = it },
            onSearch = { viewModel.searchMedicine(apiKey, keyword) },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 错误提示
        error?.let { message ->
            ErrorMessage(message)
        }

        // 结果展示
        when {
            isLoading -> LoadingIndicator()
            instructions.isNotEmpty() -> MedicineInstructionList(instructions)
            else -> EmptyPlaceholder()
        }
    }
}

@Composable
private fun MedicineSearchBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        label = { Text("输入药品名称（如：阿奇霉素）") },
        trailingIcon = {
            if (keyword.isNotEmpty()) {
                IconButton(
                    onClick = onSearch,
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
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
private fun MedicineInstructionList(instructions: List<MedicineInstruction>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(instructions) { medicine ->
            MedicineInstructionCard(medicine)
        }
    }
}

@Composable
private fun MedicineInstructionCard(medicine: MedicineInstruction) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = medicine.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        "展开/收起"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 基本信息
            InfoRow("规格", medicine.safeSpecification)
            InfoRow("用法用量", medicine.safeUsage)

            // 展开详情
            AnimatedVisibility(visible = expanded) {
                Column {
                    InfoRow("不良反应", medicine.safeSideEffects)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = medicine.content,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label：",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, MaterialTheme.colors.error),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colors.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text("输入药品名称查询说明书")
        }
    }
}