package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.log
import java.util.*
import kotlin.math.sqrt

class NewsRecommender(private val context: Context) {
    // 文档频率缓存（word -> 包含该词的文档数）
    private val docFrequency = mutableMapOf<String, Int>()
    private var totalDocs = 0
    
    // 初始化时加载文档频率数据
    init {
        loadDocumentFrequency()
    }
    
    // 加载文档频率数据（简化版，实际应用中应从服务器获取）
    private fun loadDocumentFrequency() {
        // 这里可以预加载一些基础数据
        docFrequency["科技"] = 100
        docFrequency["财经"] = 80
        docFrequency["体育"] = 70
        totalDocs = 1000 // 假设总文档数
    }

    // 基于TF-IDF和用户画像的推荐
    suspend fun getPersonalizedRecommendations(
        allNews: List<News>,
        history: List<News>,
        userInterests: Map<String, Float>,
        topN: Int = 5
    ): List<News> = withContext(Dispatchers.Default) {
        if (history.isEmpty() || allNews.isEmpty()) return@withContext emptyList()

        // 1. 计算用户兴趣向量
        val userVector = buildUserInterestVector(userInterests)
        
        // 2. 计算候选新闻与用户兴趣的匹配度
        val scoredNews = allNews
            .filter { news -> !history.any { it.id == news.id } } // 排除已读
            .map { news ->
                val newsVector = getTfIdfVector(news)
                val contentScore = cosineSimilarity(userVector, newsVector)
                
                // 加入类别偏好（用户可能偏好某些类别）
                val categoryBonus = userInterests[news.category] ?: 0f
                
                news to (contentScore * 0.4 + categoryBonus * 0.6) // 加权得分
            }

        // 3. 返回得分最高的新闻
        scoredNews.sortedByDescending { it.second }
            .take(topN)
            .map { it.first }
    }

    // 构建用户兴趣向量
    private fun buildUserInterestVector(interests: Map<String, Float>): Map<String, Double> {
        return interests.mapValues { (_, score) -> score.toDouble() }
    }

    // 计算余弦相似度
    private fun cosineSimilarity(
        vec1: Map<String, Double>,
        vec2: Map<String, Double>
    ): Double {
        val dotProduct = vec1.entries.sumOf { (term, score1) ->
            score1 * (vec2[term] ?: 0.0)
        }
        
        val norm1 = sqrt(vec1.values.sumOf { it * it })
        val norm2 = sqrt(vec2.values.sumOf { it * it })
        
        return if (norm1 > 0 && norm2 > 0) dotProduct / (norm1 * norm2) else 0.0
    }

    // 获取新闻的TF-IDF向量
    private fun getTfIdfVector(news: News): Map<String, Double> {
        val terms = extractAndCleanText(news).toTermFrequencyMap()
        
        return terms.mapValues { (term, count) ->
            val tf = count.toDouble() // 词频
            val df = docFrequency.getOrDefault(term, 1).toDouble() // 文档频率
            val idf = log((totalDocs + 1) / (df + 1)) // 逆文档频率
            
            tf * idf
        }
    }

    // 提取并清理新闻文本
    private fun extractAndCleanText(news: News): String {
        return fromHtml("${news.title} ${news.category}", FROM_HTML_MODE_LEGACY)
            .toString()
            .lowercase(Locale.getDefault())
            .replace(Regex("[^a-z0-9\\s]"), "")
    }

    // 辅助函数：将文本转换为词频图
    private fun String.toTermFrequencyMap(): Map<String, Int> {
        return this.split("\\s+".toRegex())
            .filter { it.length > 2 } // 忽略短词
            .groupingBy { it }
            .eachCount()
    }
}