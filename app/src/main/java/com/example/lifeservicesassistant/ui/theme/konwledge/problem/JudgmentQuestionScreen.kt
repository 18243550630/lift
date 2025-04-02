// JudgmentQuestionScreen.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading


@Composable
fun JudgmentQuestionScreen(
    viewModel: JudgmentQuestionViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchQuestion()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("判断题") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.fetchQuestion()
                        selectedAnswer = null
                        isAnswerRevealed = false
                    }) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessageWithRetry(
                    error = state.error!!,
                    onRetry = { viewModel.fetchQuestion() }
                )
                state.question != null -> QuestionContent(
                    question = state.question!!,
                    selectedAnswer = selectedAnswer,
                    isAnswerRevealed = isAnswerRevealed,
                    onAnswerSelected = { answer ->
                        selectedAnswer = answer
                        isAnswerRevealed = true
                        viewModel.selectAnswer(answer)
                    },
                    showAnalysis = state.showAnalysis
                )
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("点击刷新获取题目")
                }
            }
        }
    }
}

@Composable
private fun ErrorMessageWithRetry(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
private fun QuestionContent(
    question: JudgmentQuestionItem,
    selectedAnswer: Int?,
    isAnswerRevealed: Boolean,
    onAnswerSelected: (Int) -> Unit,
    showAnalysis: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 题目
        Text(
            text = question.title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        
        // 选项
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JudgmentOption(
                text = "正确",
                isSelected = selectedAnswer == 1,
                isCorrect = question.answer == 1,
                isAnswerRevealed = isAnswerRevealed,
                onClick = { onAnswerSelected(1) }
            )
            
            JudgmentOption(
                text = "错误",
                isSelected = selectedAnswer == 0,
                isCorrect = question.answer == 0,
                isAnswerRevealed = isAnswerRevealed,
                onClick = { onAnswerSelected(0) }
            )
        }
        
        // 答案解析
        if (showAnalysis && isAnswerRevealed) {
            Card(
                elevation = 4.dp,
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "答案解析",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = question.analyse,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

@Composable
private fun JudgmentOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswerRevealed: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isAnswerRevealed -> MaterialTheme.colors.surface
        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        isSelected -> Color(0xFFF44336).copy(alpha = 0.2f)
        else -> MaterialTheme.colors.surface
    }
    
    val borderColor = when {
        isSelected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
    }
    
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        border = ButtonDefaults.outlinedBorder.copy(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !isAnswerRevealed,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = !isAnswerRevealed,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.body1
            )
            
            if (isAnswerRevealed) {
                Spacer(modifier = Modifier.weight(1f))
                if (isCorrect) {
                    Text(
                        text = "✓ 正确答案",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                } else if (isSelected) {
                    Text(
                        text = "✗ 错误答案",
                        color = Color(0xFFF44336),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}