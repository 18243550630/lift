// PetScreen.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PetScreen(
    viewModel: PetViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    var selectedPetType by remember { mutableStateOf<Int?>(null) }
    val scrollState = rememberScrollState()



    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == state.petItems.size - 1 && state.canLoadMore && !state.isLoading) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("宠物百科") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
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
            // 搜索栏
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("输入宠物名称") },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.searchPets(
                                name = searchText.takeIf { it.isNotEmpty() },
                                type = selectedPetType
                            )
                        }) {
                            Icon(Icons.Default.Search, "搜索")
                        }
                    },
                    singleLine = true
                )
            }

            // 宠物类型筛选
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "全部" to null,
                    "猫科" to 0,
                    "犬类" to 1,
                    "爬行类" to 2,
                    "小宠物" to 3,
                    "水族类" to 4
                ).forEach { (text, type) ->
                    FilterChip(
                        selected = selectedPetType == type,
                        onClick = {
                            selectedPetType = type
                            viewModel.searchPets(
                                name = searchText.takeIf { it.isNotEmpty() },
                                type = type
                            )
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(text)
                    }
                }
            }

            when {
                state.isLoading && state.petItems.isEmpty() -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!) {
                    viewModel.searchPets(
                        name = searchText.takeIf { it.isNotEmpty() },
                        type = selectedPetType
                    )
                }
                state.petItems.isEmpty() -> Placeholder(searchText.isNotEmpty())
                else -> PetList(state.petItems, listState, state.isLoading)
            }
        }
    }
}

@Composable
private fun Placeholder(hasSearched: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasSearched) "暂无宠物信息，换个关键词试试" else "请输入宠物名称查询信息",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun PetList(
    items: List<PetItem>,
    listState: LazyListState,
    isLoading: Boolean
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            PetCard(item)
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PetCard(item: PetItem) {
    val testUrl = "https://via.placeholder.com/150"
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 宠物名称和类型
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item.coverURL?.let { url ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                      
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.engName ?: ""} · ${PetType.fromId(item.pettype).name}",
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 基本信息表格
            InfoTable(
                listOf(
                    "祖籍" to item.nation,
                    "寿命" to item.life,
                    "价格" to item.price,
                    "易患病" to item.easyOfDisease
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            LaunchedEffect(item.coverURL) {
                Log.d("ImageDebug", "Loading image: ${item.coverURL}")
            }

            // 详细信息部分
            ExpandableSection(title = "性格特点", content = item.characters)
            ExpandableSection(title = "体态特征", content = item.feature)
            ExpandableSection(title = "照顾须知", content = item.careKnowledge)
            ExpandableSection(title = "喂养注意", content = item.feedPoints)
        }
    }
}

@Composable
private fun InfoTable(items: List<Pair<String, String?>>) {
    val validItems = items.filter { it.second != null }
    if (validItems.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        validItems.forEachIndexed { index, (title, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value ?: "",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )
            }
            if (index < validItems.size - 1) {
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}
@Composable
fun ErrorMessage(error: String, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error, color = Color.Red)
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}

@Composable
private fun ExpandableSection(title: String, content: String?) {
    var expanded by remember { mutableStateOf(false) }
    
    if (content.isNullOrEmpty()) return
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "收起" else "展开"
            )
        }

        if (expanded) {
            Text(
                text = content,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}