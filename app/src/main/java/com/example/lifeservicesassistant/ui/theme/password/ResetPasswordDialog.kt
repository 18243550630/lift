package com.example.lifeservicesassistant.ui.theme.password

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun ResetPasswordDialog(
    onDismiss: () -> Unit,
    onPasswordReset: () -> Unit,
    viewModel: PasswordViewModel
) {
    var phoneInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var phoneVerified by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("重置查看密码") },
        text = {
            Column {
                if (!phoneVerified) {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("请输入设置时的手机号") }
                    )
                    if (showError) {
                        Text("手机号不匹配", color = Color.Red)
                    }
                } else {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("设置新查看密码") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (!phoneVerified) {
                    if (viewModel.isPhoneMatch(phoneInput)) {
                        phoneVerified = true
                        showError = false
                    } else {
                        showError = true
                    }
                } else {
                    viewModel.resetViewPassword(newPassword)
                    onPasswordReset()
                }
            }) {
                Text(if (!phoneVerified) "验证" else "重置")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
