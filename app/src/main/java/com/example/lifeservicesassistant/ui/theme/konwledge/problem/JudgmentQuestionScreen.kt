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
    var autoNextEnabled by remember { mutableStateOf(false) }

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
            // 自动下一题开关
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "自动下一题（5秒后）",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = autoNextEnabled,
                    onCheckedChange = {
                        autoNextEnabled = it
                        viewModel.setAutoNextEnabled(it)
                    }
                )
            }

            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessageWithRetry(
                    error = state.error!!,
                    onRetry = { viewModel.fetchQuestion() }
                )
                state.question != null -> QuestionContent(
                    question = state.question!!,
                    viewModel = viewModel,
                    countdown = state.countdown
                )
                else -> Placeholder()
            }
        }
    }
}

@Composable
private fun AnalysisContent(
    question: JudgmentQuestionItem,
    selectedAnswer: Int?,
    countdown: Int?
) {
    Card(
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "答案解析",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
                countdown?.let {
                    Text(
                        text = "下一题: ${it}s",
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Text(
                text = question.analyse,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 8.dp)
            )
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
private fun Placeholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("点击刷新获取题目")
    }
}

@Composable
private fun QuestionContent(
    question: JudgmentQuestionItem,
    viewModel: JudgmentQuestionViewModel,
    countdown: Int?
) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    val isAnswerRevealed = selectedAnswer != null

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = question.title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            JudgmentOption(
                text = "正确",
                isSelected = selectedAnswer == 1,
                isCorrect = question.answer == 1,
                isAnswerRevealed = isAnswerRevealed,
                onClick = {
                    selectedAnswer = 1
                    viewModel.selectAnswer(1)
                }
            )
            JudgmentOption(
                text = "错误",
                isSelected = selectedAnswer == 0,
                isCorrect = question.answer == 0,
                isAnswerRevealed = isAnswerRevealed,
                onClick = {
                    selectedAnswer = 0
                    viewModel.selectAnswer(0)
                }
            )
        }
        if (isAnswerRevealed) {
            AnalysisContent(
                question = question,
                selectedAnswer = selectedAnswer,
                countdown = countdown
            )
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