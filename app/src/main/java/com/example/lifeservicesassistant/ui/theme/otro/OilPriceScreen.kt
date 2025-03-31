// OilPriceScreen.kt
package com.example.lifeservicesassistant.ui.theme.otro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading
import com.example.lifeservicesassistant.ui.theme.play.ErrorMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OilPriceScreen(
    viewModel: OilPriceViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val provinces = remember { viewModel.getProvinces() }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("实时油价") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (state.oilPrice != null) {
                        IconButton(onClick = { 
                            viewModel.fetchOilPrice(state.selectedProvince) 
                        }) {
                            Icon(Icons.Default.Refresh, "刷新")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 省份选择器
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(state.selectedProvince)
                    Icon(
                        if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    provinces.forEach { province ->
                        DropdownMenuItem(onClick = {
                            viewModel.fetchOilPrice(province)
                            expanded = false
                        }) {
                            Text(province)
                        }
                    }
                }
            }

            // 内容区域
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!) {
                    viewModel.fetchOilPrice(state.selectedProvince)
                }
                state.oilPrice != null -> OilPriceContent(state.oilPrice!!)
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("请选择省份查询油价")
                }
            }
        }
    }
}

@Composable
private fun OilPriceContent(oilPrice: OilPriceResult) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题和更新时间
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${oilPrice.prov}油价",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "更新: ${formatTime(oilPrice.time)}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }

        // 油价卡片
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PriceCard("92号汽油", oilPrice.p92, Color(0xFF4CAF50))
            }
            item {
                PriceCard("95号汽油", oilPrice.p95, Color(0xFF2196F3))
            }
            item {
                PriceCard("98号汽油", oilPrice.p98, Color(0xFF9C27B0))
            }
            item {
                PriceCard("0号柴油", oilPrice.p0, Color(0xFF607D8B))
            }
            item {
                PriceCard("89号汽油", oilPrice.p89, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
private fun PriceCard(type: String, price: String, color: Color) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                style = MaterialTheme.typography.h6,
                color = color
            )
            Text(
                text = "$price 元/升",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

private fun formatTime(timeString: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(timeString.substringBefore("."), formatter)
        DateTimeFormatter.ofPattern("MM-dd HH:mm").format(dateTime)
    } catch (e: Exception) {
        timeString
    }
}