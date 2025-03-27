package com.example.lifeservicesassistant.ui.theme.healthy

import com.google.gson.annotations.SerializedName

// Recipe.kt
data class RecipeResponse(
    val code: Int,
    val msg: String,
    val result: RecipeResult
)

data class RecipeResult(
    val list: List<Recipe>
)

data class Recipe(
    val id: Int,
    @SerializedName("type_id")
    val typeId: Int,
    @SerializedName("type_name")
    val typeName: String,
    @SerializedName("cp_name")
    val name: String,
    val zuofa: String, // 做法
    val texing: String, // 特性
    val tishi: String, // 提示
    val tiaoliao: String, // 调料
    val yuanliao: String // 原料
)