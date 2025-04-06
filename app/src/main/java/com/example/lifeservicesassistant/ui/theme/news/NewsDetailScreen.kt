package com.example.lifeservicesassistant.ui.theme.news

import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

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
    var showMultiSelectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(news.id) {
        NewsStorage.addHistory(context, news)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val isFavorite = NewsStorage.getFavoriteFolders(context).any {
                        it.value.any { item -> item.id == news.id }
                    }

                    val favoriteText = if (isFavorite) "取消收藏" else "收藏"

                    Button(onClick = {
                        if (isFavorite) {
                            val folders = NewsStorage.getFavoriteFolders(context)
                            folders.forEach { (folder, list) ->
                                list.removeIf { it.id == news.id }
                            }
                            NewsStorage.saveFavoriteFolders(context, folders)
                            Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show()
                        } else {
                            showMultiSelectDialog = true
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

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(news.url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showMultiSelectDialog) {
        MultiSelectFolderDialog(
            context = context,
            onDismiss = { showMultiSelectDialog = false },
            onConfirm = { folderNames ->
                folderNames.forEach { folder ->
                    NewsStorage.addFavoriteToFolder(context, folder, news)
                }
                Toast.makeText(context, "已收藏到: ${folderNames.joinToString()}", Toast.LENGTH_SHORT).show()
                showMultiSelectDialog = false
            }
        )
    }
}
