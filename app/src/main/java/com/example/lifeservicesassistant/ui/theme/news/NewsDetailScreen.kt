package com.example.lifeservicesassistant.ui.theme.news

import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun NewsDetailScreen(navController: NavController) {
    val news = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<News>("selectedNews")

    if (news == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("未找到新闻内容")
        }
        return
    }

    val context = LocalContext.current

    // 自动记录浏览历史
    LaunchedEffect(news.id) {
        NewsStorage.addHistory(context, news)
    }

    Scaffold(
/*        topBar = {
            TopAppBar(
                title = { Text("新闻详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }*/
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 图片（如有）
     /*       news.urlToImage?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }*/

            // 标题 + 作者 + 日期
            Column(modifier = Modifier.padding(12.dp)) {
             /*   Text(text = news.title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${news.source}  •  ${news.publishedAt.formatDate()}",
                    style = MaterialTheme.typography.caption
                )

                Spacer(modifier = Modifier.height(12.dp))*/

                // 操作按钮：收藏 + 分享
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val isFavorite = NewsStorage.getFavorites(context).any { it.id == news.id }
                    val favoriteText = if (isFavorite) "取消收藏" else "收藏"

                    Button(onClick = {
                        if (isFavorite) {
                            val list = NewsStorage.getFavorites(context)
                            list.removeIf { it.id == news.id }
                            NewsStorage.saveFavorites(context, list)
                            Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show()
                        } else {
                            NewsStorage.addFavorite(context, news)
                            Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(favoriteText)
                    }

                    OutlinedButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, news.title)
                            putExtra(Intent.EXTRA_TEXT, news.url)
                        }
                        context.startActivity(Intent.createChooser(intent, "分享到"))
                    }) {
                        Text("分享")
                    }
                }
            }

            // 网页正文
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(news.url)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
