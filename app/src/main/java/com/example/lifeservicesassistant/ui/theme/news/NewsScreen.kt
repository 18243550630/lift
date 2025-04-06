package com.example.lifeservicesassistant.ui.theme.news

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NewsScreen() {
    val navController = rememberNavController()
    val viewModel: NewsViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("新闻服务") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        backgroundColor = MaterialTheme.colors.background
    ) {
        BottomNavGraph(navController = navController, viewModel = viewModel)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.News,
        BottomNavItem.Favorites,
        BottomNavItem.History
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

sealed class BottomNavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object News : BottomNavItem("新闻", "newsList", Icons.Default.List)
    object Favorites : BottomNavItem("收藏", "favorites", Icons.Default.Favorite)
    object History : BottomNavItem("历史", "history", Icons.Default.History)
}

@Composable
fun BottomNavGraph(navController: NavHostController, viewModel: NewsViewModel) {
    val context = LocalContext.current
    NavHost(navController, startDestination = BottomNavItem.News.route) {
        composable(BottomNavItem.News.route) {
            NewsListScreen(viewModel = viewModel, onNewsClick = { news ->
                navController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                navController.navigate("newsDetail")
            })
        }
        composable("newsDetail") {
            NewsDetailScreen(navController = navController)
        }
        composable(BottomNavItem.Favorites.route) {
            FavoriteFolderScreen(onFolderClick = { folderName ->
                navController.navigate("folderDetail/$folderName")
            })
        }
        composable("folderDetail/{folderName}") { backStackEntry ->
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            val newsList = remember { mutableStateListOf<News>().apply { addAll(NewsStorage.getFavoritesInFolder(context, folderName)) } }

            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        newsList.clear()
                        NewsStorage.saveFavoriteFolders(context, NewsStorage.getFavoriteFolders(context).apply {
                            put(folderName, mutableListOf())
                        })
                        Toast.makeText(context, "已清空该收藏夹", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("清空")
                    }
                }
                NewsList(
                    newsList = newsList,
                    title = folderName,
                    onItemClick = { news ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                        navController.navigate("newsDetail")
                    },
                    onDeleteClick = { news ->
                        newsList.remove(news)
                        NewsStorage.removeFavoriteFromFolder(context, folderName, news.id)
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        composable(BottomNavItem.History.route) {
            val history = remember { mutableStateListOf<News>().apply { addAll(NewsStorage.getHistory(context).reversed()) } }
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        history.clear()
                        NewsStorage.saveHistory(context, history)
                        Toast.makeText(context, "已清空历史", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("清空历史")
                    }
                }
                NewsList(
                    newsList = history,
                    title = "浏览历史",
                    onItemClick = { news ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                        navController.navigate("newsDetail")
                    },
                    onDeleteClick = { news ->
                        history.remove(news)
                        NewsStorage.saveHistory(context, history)
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun NewsList(newsList: List<News>, title: String, onItemClick: (News) -> Unit = {}, onDeleteClick: (News) -> Unit = {}) {
    if (newsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无$title")
        }
    } else {
        LazyNewsList(newsList = newsList, onItemClick = onItemClick, onDeleteClick = onDeleteClick)
    }
}

@Composable
fun LazyNewsList(newsList: List<News>, onItemClick: (News) -> Unit = {}, onDeleteClick: (News) -> Unit = {}) {
    val context = LocalContext.current
    var toDelete by remember { mutableStateOf<News?>(null) }
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm && toDelete != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除该条新闻吗？") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick(toDelete!!)
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                    showConfirm = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }

    androidx.compose.foundation.lazy.LazyColumn {
        items(newsList.size) { index ->
            val news = newsList[index]
            var offsetX by remember { mutableStateOf(0f) }

            Box(modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        offsetX += dragAmount
                        if (offsetX < -150f) {
                            toDelete = news
                            showConfirm = true
                            offsetX = 0f
                        }
                    }
                }
                .background(Color.Transparent)
                .padding(8.dp)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onItemClick(news) }.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NewsItem(news = news, onClick = { onItemClick(news) })
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "滑动删除",
                        modifier = Modifier
                            .alpha((-offsetX / 100f).coerceIn(0f, 1f))
                            .scale((-offsetX / 100f).coerceIn(0.8f, 1.2f))
                            .padding(start = 8.dp),
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
