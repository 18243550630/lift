package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SudokuBoard(
    gameState: SudokuGame,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        repeat(9) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(9) { col ->
                    val cellValue = gameState.getCellValue(row, col)
                    val isOriginal = gameState.puzzle[row][col] != 0
                    val isSelected = selectedCell?.let { it.first == row && it.second == col } ?: false
                    val backgroundColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)  // 选中状态高亮
                        (row / 3 + col / 3) % 2 == 0 -> MaterialTheme.colorScheme.surfaceVariant  // 宫格交替色
                        else -> MaterialTheme.colorScheme.surface
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(
                                width = when {
                                    row % 3 == 0 && col % 3 == 0 -> 2.dp  // 宫格左上角粗边框
                                    row % 3 == 0 -> 1.dp  // 行粗边框
                                    col % 3 == 0 -> 1.dp  // 列粗边框
                                    else -> 0.5.dp  // 细边框
                                },
                                color = MaterialTheme.colorScheme.outline
                            )
                            .clickable { onCellClick(row, col) }
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellValue != 0) {
                            Text(
                                text = cellValue.toString(),
                                color = if (isOriginal) MaterialTheme.colorScheme.onSurface  // 原始数字颜色
                                else MaterialTheme.colorScheme.primary,  // 用户输入数字颜色
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}