// PoetryQuestionScreen.kt
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
fun PoetryQuestionScreen(
    viewModel: PoetryQuestionViewModel,
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
                title = { Text("诗词问答") },
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
private fun QuestionContent(
    question: PoetryQuestionItem,
    viewModel: PoetryQuestionViewModel,
    countdown: Int?
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val isAnswerRevealed = selectedAnswer != null

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 问题
        Text(
            text = question.question,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        
        // 选项
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PoetryOption(
                option = "A",
                text = question.answer_a,
                isSelected = selectedAnswer == "A",
                isCorrect = question.answer == "A",
                isAnswerRevealed = isAnswerRevealed,
                onClick = { 
                    selectedAnswer = "A"
                    viewModel.selectAnswer("A")
                }
            )
            
            PoetryOption(
                option = "B",
                text = question.answer_b,
                isSelected = selectedAnswer == "B",
                isCorrect = question.answer == "B",
                isAnswerRevealed = isAnswerRevealed,
                onClick = { 
                    selectedAnswer = "B"
                    viewModel.selectAnswer("B")
                }
            )
            
            PoetryOption(
                option = "C",
                text = question.answer_c,
                isSelected = selectedAnswer == "C",
                isCorrect = question.answer == "C",
                isAnswerRevealed = isAnswerRevealed,
                onClick = { 
                    selectedAnswer = "C"
                    viewModel.selectAnswer("C")
                }
            )
        }
        
        // 答案解析
        if (isAnswerRevealed) {
            AnalysisContent(
                analysis = question.analytic,
                countdown = countdown
            )
        }
    }
}

@Composable
private fun PoetryOption(
    option: String,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$option. $text",
                    style = MaterialTheme.typography.body1
                )
            }
            
            if (isAnswerRevealed) {
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

@Composable
private fun AnalysisContent(
    analysis: String,
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
                text = analysis,
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