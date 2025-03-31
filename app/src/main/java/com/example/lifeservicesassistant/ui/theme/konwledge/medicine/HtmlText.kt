package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val annotatedString = remember(html) {
        buildAnnotatedString {
            append(
                html.replace("&nbsp;", " ")
                    .replace("<br\\s?\\/?>".toRegex(), "\n")
            )
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.body2
    )
}