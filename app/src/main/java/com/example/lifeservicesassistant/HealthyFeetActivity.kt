package com.example.lifeservicesassistant

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.lifeservicesassistant.ui.theme.healthy.StepCounterScreen
import com.example.lifeservicesassistant.util.StepCounterTheme


class HealthyFeetActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var initialStepCount = -1
    private var stepCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        /* setContent {
            StepCounterTheme {
                StepCounterScreen(
                    stepCount = stepCount,
                    onNavigateBack = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }*/
        setContent {
            StepCounterTheme {
                StepCounterScreen(
                    stepCount = 4500, // 示例步数
                    onNavigateBack = { finish() },
                    context = this
                )
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toInt()
            if (initialStepCount == -1) {
                initialStepCount = steps
            }
            stepCount = steps - initialStepCount
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}