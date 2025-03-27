package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun SudokuScreen(viewModel: SudokuViewModel = viewModel()) {
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 难度选择
        DifficultySelector { difficulty ->
            viewModel.generateNewGame(difficulty)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 游戏板
        viewModel.gameState?.let { gameState ->
            SudokuBoard(
                gameState = gameState,
                selectedCell = selectedCell,
                onCellClick = { row, col ->
                    selectedCell = row to col
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 数字键盘
            selectedCell?.let { (row, col) ->
                NumberPad(
                    onNumberSelected = { num ->
                        viewModel.updateCell(row, col, num)
                    },
                    onClear = {
                        viewModel.updateCell(row, col, null)
                    }
                )
            }
        } ?: run {
            if (viewModel.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("请选择难度开始游戏")
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberSelected: (Int) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            (1..5).forEach { num ->
                NumberButton(num, onNumberSelected)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            (6..9).forEach { num ->
                NumberButton(num, onNumberSelected)
            }
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.size(24.dp))

            }
        }
    }
}

@Composable
private fun NumberButton(number: Int, onClick: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .padding(4.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        onClick = { onClick(number) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)  // 确保背景透明
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontSize = 24.sp,  // 进一步增大字体
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(0.dp)
                    .drawWithContent { drawContent() }  // 强制绘制内容
            )
        }
    }
}
@Composable
fun DifficultySelector(onDifficultySelected: (String) -> Unit) {
    val difficulties = listOf("简单", "普通", "困难", "亚洲")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        difficulties.forEach { difficulty ->
            Button(onClick = { onDifficultySelected(difficulty) }) {
                Text(text = difficulty.capitalize())
            }
        }
    }
}