// HotNewsScreen.kt
package com.example.lifeservicesassistant.ui.theme.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.EmptyPlaceholder

@Composable
fun HotNewsScreen(
    viewModel: HotNewsViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("全网热搜") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (!state.isLoading && state.error == null) {
                        TextButton(onClick = { viewModel.fetchHotNews(apiKey) }) {
                            Text("刷新")
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
        ) {
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!) {
                    viewModel.fetchHotNews(apiKey)
                }
                state.hotNewsItems.isEmpty() -> EmptyPlaceholder()
                else -> HotNewsList(state.hotNewsItems)
            }
        }
    }
}

@Composable
private fun HotNewsList(items: List<HotNewsItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            HotNewsCard(item)
        }
    }
}

@Composable
private fun HotNewsCard(item: HotNewsItem) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "🔥${item.hotnum}",
                    color = MaterialTheme.colors.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            item.digest?.let { digest ->
                Text(
                    text = digest,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}