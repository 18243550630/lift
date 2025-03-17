package com.example.lifeservicesassistant.ui.theme.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.logic.model.User
import com.example.lifeservicesassistant.logic.model.UserDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(userId: Int, dbHelper: UserDbHelper) {
    var user by remember { mutableStateOf<User?>(null) }
    var username by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        withContext(Dispatchers.IO) {
            // 根据ID获取用户信息
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                UserDbHelper.TABLE_NAME,
                null,
                "${UserDbHelper.COL_ID} = ?",
                arrayOf(userId.toString()),
                null, null, null
            )
            user = if (cursor.moveToFirst()) {
                User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COL_ID)),
                    account = cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COL_ACCOUNT)),
                    password = "",
                    username = cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COL_USERNAME)),
                    avatar = cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COL_AVATAR)),
                    registerTime = cursor.getLong(cursor.getColumnIndexOrThrow(UserDbHelper.COL_REGISTER_TIME)),
                    status = cursor.getInt(cursor.getColumnIndexOrThrow(UserDbHelper.COL_STATUS))
                )
            } else null
            cursor.close()
        }
    }

    user?.let { 
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = avatarUrl.ifEmpty { R.drawable.bg_clear_day},
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                        placeholder = painterResource(R.drawable.bg_clear_day), // 添加占位图
                error = painterResource(R.drawable.bg_clear_day) // 添加错误图
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val updatedUser = user!!.copy(username = username, avatar = avatarUrl)
                    dbHelper.updateUserInfo(updatedUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("保存修改")
            }
        }
    }
}