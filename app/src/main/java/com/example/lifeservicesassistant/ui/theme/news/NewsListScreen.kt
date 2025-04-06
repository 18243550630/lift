package com.example.lifeservicesassistant.ui.theme.news

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun NewsListScreen(
    viewModel: NewsViewModel,
    onNewsClick: (News) -> Unit
) {
    val newsState = viewModel.newsState.value
    val categories = listOf("首页", "推荐", "国内", "娱乐", "体育", "科技", "财经", "时尚")
    var selectedCategory by remember { mutableStateOf("首页") }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            edgePadding = 8.dp
        ) {
            categories.forEach { category ->
                Tab(
                    selected = category == selectedCategory,
                    onClick = {
                        selectedCategory = category
                        viewModel.loadNews(category)
                    }
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        }

        when (newsState) {
            is NewsViewModel.NewsState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is NewsViewModel.NewsState.Success -> {
                LazyColumn(state = listState) {
                    // 推荐 Tab 显示推荐内容
                    if (selectedCategory == "为你推荐") {
                        if (newsState.news.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("暂无推荐内容，请多阅读你喜欢的新闻~")
                                }
                            }
                        } else {
                            items(newsState.news) { news ->
                                NewsItem(news = news, onClick = { onNewsClick(news) })
                                Divider()
                            }
                        }
                    } else {
                        items(newsState.news) { news ->
                            NewsItem(news = news, onClick = { onNewsClick(news) })
                            Divider()
                        }
                    }
                }

                // 监听推荐滑动加载更多
                val shouldLoadMore = remember {
                    derivedStateOf {
                        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisible >= listState.layoutInfo.totalItemsCount - 3
                    }
                }

                LaunchedEffect(shouldLoadMore.value, selectedCategory) {
                    if (selectedCategory == "推荐" && shouldLoadMore.value) {
                        viewModel.loadMoreRecommendations()
                    }
                }
            }

            is NewsViewModel.NewsState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Error: ${newsState.message}", color = MaterialTheme.colors.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadNews(selectedCategory) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadNews(selectedCategory)
    }
}

@Composable
fun NewsItem(news: News, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            news.urlToImage?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = news.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.source,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.publishedAt.formatDate(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

fun String.formatDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
        val date = inputFormat.parse(this)
        outputFormat.format(date)
    } catch (e: Exception) {
        this
    }
}
