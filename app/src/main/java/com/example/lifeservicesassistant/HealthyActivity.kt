package com.example.lifeservicesassistant

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lifeservicesassistant.util.StepCounterTheme
import com.example.lifeservicesassistant.ui.theme.healthy.HealthScreen
import com.example.lifeservicesassistant.util.StepPreferences

class HealthyActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCount = 0
    private var dailyGoal = 6000  // 默认目标步数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化传感器管理器
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            // 如果设备不支持步数传感器，使用默认步数
            stepCount = 4500
        }

        // 从 SharedPreferences 获取目标步数
        dailyGoal = StepPreferences.getGoalStep(this)

        setContent {
            StepCounterTheme {
                // 将步数和目标步数传递到 HealthScreen
                HealthScreen(
                    stepCount = stepCount,
                    dailyGoal = dailyGoal,
                    onNavigateBack = {
                        // 返回到 MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()  // 结束当前 Activity
                    }
                )
            }
        }
    }

    // 实现 SensorEventListener 接口
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            // 获取步数传感器数据
            stepCount = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 不需要处理传感器准确度的变化
    }

    // 确保注销传感器监听器
    override fun onResume() {
        super.onResume()
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}




