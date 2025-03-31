package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import androidx.core.text.HtmlCompat

object HtmlUtils {
    fun parseMedicineContent(html: String): String {
        return HtmlCompat.fromHtml(
            html.replace("<br\\s?/?>".toRegex(), "\n")
                .replace("&nbsp;", " ")
                .replace("【", "\n【"),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toString().trim()
    }

    fun extractSection(content: String, section: String): String? {
        val pattern = "$section\\s*[\\n\\r]+(.*?)(\\n\\s*【|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        return pattern.find(content)?.groupValues?.get(1)?.trim()
    }
}