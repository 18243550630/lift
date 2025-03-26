package com.example.lifeservicesassistant.ui.theme.healthy

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NutrientScreen(
    viewModel: NutrientViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    var keyword by remember { mutableStateOf("") }
    val nutrientList by viewModel.nutrientList.collectAsState()
    val isLoading by viewModel._isLoading.collectAsState()
    var itemsPerPage by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // 顶部栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "返回")
            }
            Text("营养查询", style = MaterialTheme.typography.h5)
        }

        Spacer(Modifier.height(16.dp))

        // 搜索栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("输入食物名称") },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { viewModel.fetchNutrient(apiKey, keyword, num = itemsPerPage) },
                enabled = keyword.isNotBlank() && !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("搜索")
            }
        }

        // 分页设置
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("每页数量:", modifier = Modifier.padding(end = 8.dp))
            listOf(5, 10, 20).forEach { num ->
                FilterChip(
                    selected = itemsPerPage == num,
                    onClick = { itemsPerPage = num },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(num.toString())
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 结果列表
        LazyColumn {
            items(nutrientList) { item ->
                NutrientCard(item) {
                    viewModel.toggleExpand(item.name)
                }
            }

            item {
                if (nutrientList.isNotEmpty()) {
                    Button(
                        onClick = { viewModel.loadNextPage(apiKey, keyword, itemsPerPage) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(Modifier.size(20.dp))
                        Text("加载更多")
                    }
                }
            }
        }

        // 空状态
        if (nutrientList.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无数据，请输入关键词搜索", color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun NutrientCard(item: NutrientInfo, onToggle: () -> Unit) {

    val borderColor = if (item.isExpanded) {
        MaterialTheme.colors.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    }


    Card(
        elevation = 0.dp, // 取消阴影改用边框
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface,
        border = BorderStroke(
            width = if (item.isExpanded) 1.5.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column(Modifier.padding(16.dp)) {
            // 标题行（保持不变）
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.name,
                        style = MaterialTheme.typography.h6.copy(
                            color = MaterialTheme.colors.primary
                        ))
                    Text(item.type,
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        ))
                }
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (item.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            // 核心指标
            NutrientGrid(
                listOf(
                    "热量" to "${item.rl} 大卡",
                    "蛋白质" to "${item.dbz}g",
                    "脂肪" to "${item.zf}g",
                    "碳水" to "${item.shhf}g"
                )
            )

            // 展开详情
            if (item.isExpanded) {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    thickness = 1.dp
                )

                // 维生素组
                Text("维生素", style = MaterialTheme.typography.subtitle1)
                NutrientGrid(
                    listOf(
                        "硫胺素" to "${item.las}mg",
                        "核黄素" to "${item.su}mg",
                        "烟酸" to "${item.ys}mg",
                        "维C" to "${item.wsfc}mg",
                        "维E" to "${item.wsse}mg",
                        "维A" to "${item.wssa}μg",
                        "胡萝卜素" to "${item.lb}μg"
                    ),
                    columns = 3
                )

                // 矿物质组
                Text("矿物质", style = MaterialTheme.typography.subtitle1)
                NutrientGrid(
                    listOf(
                        "钙" to "${item.gai}mg",
                        "铁" to "${item.tei}mg",
                        "锌" to "${item.xin}mg",
                        "铜" to "${item.tong}mg",
                        "锰" to "${item.meng}mg",
                        "钾" to "${item.jia}mg",
                        "钠" to "${item.la}mg",
                        "磷" to "${item.ling}mg",
                        "硒" to "${item.xi}μg",
                        "镁" to "${item.mei}mg"
                    ),
                    columns = 3
                )

                // 其他
                Text("其他成分", style = MaterialTheme.typography.subtitle1)
                NutrientGrid(
                    listOf(
                        "胆固醇" to "${item.dgc}mg",
                        "膳食纤维" to "${item.ssxw}g",
                        "视黄醇" to "${item.shc}μg"
                    )
                )
            }
        }
    }
}

@Composable
private fun NutrientGrid(
    items: List<Pair<String, String>>,
    columns: Int = 2,
    modifier: Modifier = Modifier
) {
    val rows = items.chunked(columns)
    Column(modifier.padding(vertical = 8.dp)) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { (label, value) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Column {
                            Text(label, fontSize = 12.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
                            Text(value, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridLayout(content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            content()
        }
    }
}

@Composable
fun NutrientDataCell(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Text(value, fontWeight = FontWeight.Medium)
    }
}