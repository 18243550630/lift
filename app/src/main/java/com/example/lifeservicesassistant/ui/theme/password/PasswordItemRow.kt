package com.example.lifeservicesassistant.ui.theme.password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PasswordItemRow(passwordItem: PasswordItem, viewModel: PasswordViewModel, bookId: Long) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                viewModel.deletePasswordItem(bookId, passwordItem.id)
            }
            true
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Red)
            }
        },
        dismissContent = {
            ListItem(
                text = { Text(passwordItem.title) },
                secondaryText = { Text(passwordItem.password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    )
}
