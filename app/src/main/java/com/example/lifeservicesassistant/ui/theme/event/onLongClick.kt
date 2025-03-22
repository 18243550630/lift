package com.example.lifeservicesassistant.ui.theme.event

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.composed

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.onLongClick(onLongClick: () -> Unit): Modifier = composed {
    this.then(
        Modifier.combinedClickable(
            onClick = {},
            onLongClick = onLongClick
        )
    )
}
