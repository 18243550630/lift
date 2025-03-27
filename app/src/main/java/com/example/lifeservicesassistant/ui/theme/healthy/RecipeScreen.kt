package com.example.lifeservicesassistant.ui.theme.healthy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
// RecipeScreen.kt
@Composable
fun RecipeScreen(viewModel: RecipeViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                isSearching = true
                viewModel.searchRecipes(searchQuery)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RecipeList(
            recipes = viewModel.recipes,
            isLoading = viewModel.isLoading.value,
            error = viewModel.error.value,
            lazyListState = lazyListState,
            onLoadMore = {
                if (!viewModel.isLoading.value && isSearching) {
                    viewModel.searchRecipes(searchQuery, isNewSearch = false)
                }
            }
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("搜索菜谱或食材") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    isLoading: Boolean,
    error: String?,
    lazyListState: LazyListState,
    onLoadMore: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (error != null) {
            ErrorMessage(error = error)
        } else if (recipes.isEmpty() && !isLoading) {
            EmptyState()
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(recipes) { recipe ->
                    RecipeItem(recipe = recipe)
                    Divider()
                }
                
                item {
                    if (isLoading) {
                        LoadingIndicator()
                    } else {
                        LaunchedEffect(Unit) {
                            if (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == 
                                lazyListState.layoutInfo.totalItemsCount - 1) {
                                onLoadMore()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (expanded) MaterialTheme.colors.surface else MaterialTheme.colors.background,
        border = BorderStroke(
            width = 1.dp,
            color = if (expanded) MaterialTheme.colors.primary.copy(alpha = 0.5f)
            else MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 添加分类图标
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "菜谱分类",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = recipe.typeName,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colors.primary.copy(alpha = 0.2f))

                // 详细信息部分
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    // 原料部分
                    InfoSection(
                        title = "原料",
                        content = recipe.yuanliao,
                        icon = Icons.Default.ShoppingCart
                    )

                    // 调料部分
                    InfoSection(
                        title = "调料",
                        content = recipe.tiaoliao,
                        icon = Icons.Default.LocalBar
                    )

                    // 做法部分
                    InfoSection(
                        title = "做法",
                        content = recipe.zuofa,
                        icon = Icons.Default.Info
                    )

                    // 特性部分（如果有）
                    if (recipe.texing.isNotEmpty()) {
                        InfoSection(
                            title = "特性",
                            content = recipe.texing,
                            icon = Icons.Default.Star
                        )
                    }

                    // 提示部分（如果有）
                    if (recipe.tishi.isNotEmpty()) {
                        InfoSection(
                            title = "小贴士",
                            content = recipe.tishi,
                            icon = Icons.Default.Lightbulb
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoSection(title: String, content: String, icon: ImageVector) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colors.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.primary
            )
        }
        Text(
            text = content,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 26.dp)
        )
    }
}




@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $error",
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = "空状态",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "没有找到相关菜谱", style = MaterialTheme.typography.h6)
            Text(text = "尝试搜索其他关键词", style = MaterialTheme.typography.body2)
        }
    }
}