package com.example.lifeservicesassistant.logic.dao

import android.content.Context

object StepPreferences {
    private const val PREFS_NAME = "StepPrefs"
    private const val KEY_GOAL_STEP = "goalStep"

    // 获取目标步数
    fun getGoalStep(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_GOAL_STEP, 10000)  // 默认值为 10000
    }

    // 保存目标步数
    fun saveGoalStep(context: Context, goal: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(KEY_GOAL_STEP, goal).apply()
    }
}
