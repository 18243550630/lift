package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.healthy.LoadingIndicator

@Composable
fun QAScreen(
    viewModel: QAViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    val currentQA by viewModel.currentQA.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // 顶部栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "返回")
            }
            Text("百科问答", style = MaterialTheme.typography.h5)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> LoadingIndicator()
            error != null -> ErrorMessage(error!!)
            currentQA == null -> EmptyPlaceholder()
            else -> QAQuestionCard(
                qa = currentQA!!,
                selectedAnswer = selectedAnswer,
                onAnswerSelected = { answer ->
                    viewModel.selectAnswer(answer)
                },
                onNextQuestion = { viewModel.fetchQA(apiKey) }
            )
        }
    }
}

@Composable
fun QAQuestionCard(
    qa: QAResult,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit
) {
    val isAnswered = selectedAnswer != null
    val isCorrect = isAnswered && selectedAnswer == qa.answer

    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = qa.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp))

            // 答案选项
            listOf(
                "A" to qa.answerA,
                "B" to qa.answerB,
                "C" to qa.answerC,
                "D" to qa.answerD
            ).forEach { (key, answer) ->
                val isSelected = selectedAnswer == key
                val isRightAnswer = isAnswered && key == qa.answer

                val backgroundColor = when {
                    isRightAnswer -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.2f)
                    isSelected -> Color(0xFF2196F3).copy(alpha = 0.2f)
                    else -> MaterialTheme.colors.surface
                }

                Card(
                    backgroundColor = backgroundColor,
                    border = BorderStroke(
                        1.dp,
                        when {
                            isRightAnswer -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> Color(0xFFF44336)
                            isSelected -> Color(0xFF2196F3)
                            else -> Color.LightGray
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(
                            enabled = !isAnswered,
                            onClick = { onAnswerSelected(key) }
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$key.",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp))

                        Text(text = answer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 答案解析
            if (isAnswered) {
                Column {
                    Text(
                        text = if (isCorrect) "✅ 回答正确" else "❌ 回答错误",
                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "解析：${qa.analytic}",
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text("下一题")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text("点击下方按钮开始答题")
        }
    }
}