package com.example.lifeservicesassistant.util

import android.content.Context

object StepPreferences {

    private const val PREFS_NAME = "step_prefs"
    private const val KEY_GOAL_STEP = "goal_step"

    // 保存目标步数
    fun saveGoalStep(context: Context, goalStep: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_GOAL_STEP, goalStep).apply()
    }

    // 读取目标步数，默认值为 10000
    fun getGoalStep(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_GOAL_STEP, 10000)
    }
}
