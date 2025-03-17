package com.example.lifeservicesassistant.ui.theme.otro

import android.media.MediaPlayer
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.util.CommonTopBar
import kotlinx.coroutines.launch

@Composable
fun TimerScreen(
    onNavigateBack: () -> Unit // 传入返回操作
) {
    val context = LocalContext.current

    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedSecond by remember { mutableStateOf(0) }

    var remainingTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var countDownTimer by remember { mutableStateOf<CountDownTimer?>(null) }

    var isPlayingSound by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.alarm_sound) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "计时器",
                onNavigateBack = onNavigateBack // 传递返回回调
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(36.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TimePicker(0..23, selectedHour) { selectedHour = it }
                        Text(text = "小时", modifier = Modifier.padding(top = 4.dp), fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TimePicker(0..59, selectedMinute) { selectedMinute = it }
                        Text(text = "分钟", modifier = Modifier.padding(top = 4.dp), fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TimePicker(0..59, selectedSecond) { selectedSecond = it }
                        Text(text = "秒", modifier = Modifier.padding(top = 4.dp), fontSize = 16.sp)
                    }
                }
            }

            Button(
                onClick = {
                    if (isRunning) {
                        countDownTimer?.cancel()
                        isRunning = false
                        remainingTime = 0
                    } else {
                        val totalMillis = (selectedHour * 3600 + selectedMinute * 60 + selectedSecond) * 1000L
                        if (totalMillis > 0) {
                            countDownTimer = object : CountDownTimer(totalMillis, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    remainingTime = millisUntilFinished / 1000
                                }

                                override fun onFinish() {
                                    isRunning = false
                                    remainingTime = 0
                                    isPlayingSound = true
                                    mediaPlayer.start()
                                    Toast.makeText(context, "时间到！", Toast.LENGTH_SHORT).show()
                                }
                            }.start()
                            isRunning = true
                        } else {
                            Toast.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3A7BD5)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isRunning) "停止" else "开始", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (isRunning) {
                AnimatedCountdown(remainingTime)
            }

            if (isPlayingSound) {
                Button(
                    onClick = {
                        mediaPlayer.stop()
                        mediaPlayer.prepare()
                        isPlayingSound = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("结束计时", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun TimePicker(range: IntRange, selectedValue: Int, onValueChange: (Int) -> Unit) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedValue)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.height(100.dp)
        ) {
            itemsIndexed(range.toList()) { index, value ->
                val isSelected = index == listState.firstVisibleItemIndex
                Text(
                    text = value.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.Black else Color.Gray
                )
            }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        coroutineScope.launch {
            onValueChange(listState.firstVisibleItemIndex )
        }
    }
}

@Composable
fun AnimatedCountdown(remainingTime: Long) {
    val animatedTime by animateIntAsState(
        targetValue = remainingTime.toInt(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    Text(
        text = "剩余时间: ${animatedTime / 3600}小时 ${animatedTime % 3600 / 60}分钟 ${animatedTime % 60}秒",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(16.dp)
    )
}