package com.example.lifeservicesassistant.ui.theme.otro

import CalculatorTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeservicesassistant.util.CommonTopBar


class ConterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp(onNavigateBack = { finish() })
                }
            }
        }
    }
}

@Composable
fun CalculatorApp(onNavigateBack: () -> Unit) {
    var result by remember { mutableStateOf("0") }
    var history by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部导航栏
        CommonTopBar(
            title = "计算器",
            onNavigateBack = onNavigateBack
        )

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .weight(1f)
    ) {
        // History Text
        Text(
            text = history,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = TextStyle(
                fontSize = 28.sp,
                color = Color.Gray,
                textAlign = TextAlign.End
            )
        )

        // Result Text
        BasicTextField(
            value = result,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = TextStyle(
                fontSize = 40.sp,
                color = Color.Black,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            ),
            readOnly = true
        )

        // Buttons Grid
        val buttons = listOf(
            listOf("C", "←", "÷", "×"),
            listOf("7", "8", "9", "-"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("%", "0", ".", "")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { buttonText ->
                    Button(
                        onClick = {
                            when (buttonText) {
                                "C" -> {
                                    result = "0"
                                    history = ""
                                }

                                "←" -> {
                                    result = if (result.length > 1) result.dropLast(1) else "0"
                                }

                                "=" -> {
                                    history = "$result="
                                    result = calculateResult(result)
                                }

                                else -> {
                                    if (result == "0" && buttonText != ".") {
                                        result = buttonText
                                    } else {
                                        result += buttonText
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (buttonText) {
                                "C" -> Color(0xFFFFA500)
                                "=" -> Color(0xFF4CAF50)
                                else -> Color(0xFFE0E0E0)
                            }
                        )
                    ) {
                        Text(
                            text = buttonText,
                            style = TextStyle(
                                fontSize = 32.sp,
                                color = when (buttonText) {
                                    "C" -> Color.White
                                    "=" -> Color.White
                                    else -> Color.Black
                                }
                            )
                        )
                    }
                }
                }
            }
        }
    }
}

fun calculateResult(expression: String): String {
    return try {
        val cleanedExpression = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("%", "/100")
        val result = eval(cleanedExpression)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun eval(expression: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0
        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }
        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }
        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
            return x
        }
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }
        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    else -> return x
                }
            }
        }
        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()
            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected: ${ch.toChar()}")
            }
            return x
        }
    }.parse()
}