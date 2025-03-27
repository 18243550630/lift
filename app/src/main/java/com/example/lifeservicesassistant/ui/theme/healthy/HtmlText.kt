package com.example.lifeservicesassistant.ui.theme.healthy// HtmlText.kt
import android.text.Html
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val annotatedString = remember(html) {
        buildAnnotatedString {
            val strippedHtml = html.replace("<br>", "\n")
                .replace("<p>", "\n")
                .replace("</p>", "")
            
            append(HtmlCompat.fromHtml(strippedHtml, HtmlCompat.FROM_HTML_MODE_COMPACT).toString())
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.body2
    )
}