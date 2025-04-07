package com.example.lifeservicesassistant.ui.theme.news

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

val FolderIcons = listOf(
    Icons.Default.Folder,
    Icons.Default.Star,
    Icons.Default.Bookmark,
    Icons.Default.Favorite,
    Icons.Default.Label
)

val FolderColors = listOf(
    Color(0xFFEF5350), // Red
    Color(0xFF66BB6A), // Green
    Color(0xFF42A5F5), // Blue
    Color(0xFFFFA726), // Orange
    Color(0xFFAB47BC)  // Purple
)

@Composable
fun FolderStyleSelector(
    selectedIcon: ImageVector,
    selectedColor: Color,
    onIconChange: (ImageVector) -> Unit,
    onColorChange: (Color) -> Unit
) {
    Column {
        Text("选择图标", style = MaterialTheme.typography.subtitle1)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FolderIcons.forEach { icon ->
                IconButton(onClick = { onIconChange(icon) }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (icon == selectedIcon) MaterialTheme.colors.primary else LocalContentColor.current
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("选择颜色", style = MaterialTheme.typography.subtitle1)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FolderColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = color, shape = CircleShape)
                        .clickable { onColorChange(color) }
                        .then(if (color == selectedColor) Modifier.border(2.dp, Color.Black, CircleShape) else Modifier)
                )
            }
        }
    }
}

@Composable
fun MoveNewsDialog(
    newsItems: List<News>,
    onMoveToFolder: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val folders = remember { NewsStorage.getFavoriteFolders(context).keys.toList() }
    var selectedFolder by remember { mutableStateOf(folders.firstOrNull() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("移动到收藏夹", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                folders.forEach { folder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFolder = folder }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedFolder == folder,
                            onClick = { selectedFolder = folder }
                        )
                        Text(folder)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        newsItems.forEach { news ->
                            NewsStorage.addFavoriteToFolder(context, selectedFolder, news)
                        }
                        onMoveToFolder(selectedFolder)
                    }) {
                        Text("确认移动")
                    }
                }
            }
        }
    }
}
