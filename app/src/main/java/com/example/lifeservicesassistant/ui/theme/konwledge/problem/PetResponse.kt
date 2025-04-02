// PetInfo.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class PetResponse(
    val code: Int,
    val msg: String,
    val result: PetResult?
)

data class PetResult(
    val list: List<PetItem>?
)

data class PetItem(
    val pettype: Int,            // 宠物类型(0猫科/1犬类等)
    val name: String,            // 宠物名称
    val engName: String?,        // 英文名
    val characters: String?,     // 性格特点
    val nation: String?,         // 祖籍
    val easyOfDisease: String?,  // 易患病
    val life: String?,           // 寿命
    val price: String?,          // 价格
    val desc: String?,           // 描述
    val feature: String?,        // 体态特征
    val characterFeature: String?, // 特点
    val careKnowledge: String?,  // 照顾须知
    val feedPoints: String?,     // 喂养注意
    val url: String?,            // 详情链接
    val coverURL: String?        // 封面图片URL
)

// 宠物类型枚举
enum class PetType(val id: Int) {
    CAT(0),       // 猫科
    DOG(1),       // 犬类
    REPTILE(2),   // 爬行类
    SMALL(3),     // 小宠物类
    AQUATIC(4);   // 水族类

    companion object {
        fun fromId(id: Int): PetType {
            return values().firstOrNull { it.id == id } ?: DOG
        }
    }
}