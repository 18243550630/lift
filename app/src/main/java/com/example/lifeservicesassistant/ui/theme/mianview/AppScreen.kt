package com.example.lifeservicesassistant.ui.theme.mianview

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeservicesassistant.ui.theme.otro.ConterActivity
import com.example.lifeservicesassistant.HealthyActivity
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.TimerActivity
import com.example.lifeservicesassistant.WeatherForecastActivity
import com.example.lifeservicesassistant.RecordActivity
import com.example.lifeservicesassistant.ui.theme.data.DataActivity
import com.example.lifeservicesassistant.ui.theme.event.EventListActivity
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.MedicineInstructionActivity
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.QAActivity
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.SynonymAntonymActivity
import com.example.lifeservicesassistant.ui.theme.konwledge.sudu.SudokuActivity
import com.example.lifeservicesassistant.ui.theme.news.NewsActivity
import com.example.lifeservicesassistant.ui.theme.news.NewsItem
import com.example.lifeservicesassistant.ui.theme.note.NoteActivity
import com.example.lifeservicesassistant.ui.theme.otro.ColorConverterActivity
import com.example.lifeservicesassistant.ui.theme.otro.DateActivity
import com.example.lifeservicesassistant.ui.theme.otro.LoginActivity
import com.example.lifeservicesassistant.ui.theme.otro.RandomNumberActivity
import com.example.lifeservicesassistant.ui.theme.otro.ScoreboardActivity
import com.example.lifeservicesassistant.ui.theme.otro.qrcode.QrCodeActivity
import com.example.lifeservicesassistant.ui.theme.password.PasswordManagementActivity

/*@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppCategoryRow(categoryName: String, iconNames: List<String>, iconImages: List<Int>) {
    var isExpanded by remember { mutableStateOf(true) } // 管理展开/收起状态

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 8.dp)
                .clickable { isExpanded = !isExpanded }, // 点击切换展开状态
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = TextStyle(fontSize = 18.sp, color = Color.Gray)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown, // 根据状态切换箭头方向
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        // 展示或隐藏图标
        if (isExpanded) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 使用 FlowRow 使图标在一行显示不下时自动换行
                iconNames.forEachIndexed { index, iconName ->
                    AppIconButton(iconName = iconName, imageRes = iconImages[index])
                }
            }
        }
    }
}*/

@Composable
fun AppIconButton(iconName: String, imageRes: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // 居中排列
        modifier = Modifier
            .padding(8.dp) // 给图标和文字周围留些空间
            .wrapContentSize() // 根据内容调整大小
    ) {
        Card(
            modifier = Modifier
                .size(60.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFf4f4f4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // 使用 Image 显示不同的本地图片
                Image(
                    painter = painterResource(id = imageRes), // 动态加载对应的图片
                    contentDescription = null,
                    modifier = Modifier.size(24.dp) // 设置图片大小
                )
            }
        }
        // 图标下方显示名称
        Text(
            text = iconName,
            style = TextStyle(fontSize = 12.sp, color = Color.Black),
            modifier = Modifier.padding(top = 4.dp) // 添加顶部间距
        )
    }
}

@Composable
fun AppScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 30.dp, start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "生活助手",
                style = TextStyle(fontSize = 28.sp, color = Color(0xFF5B8C2A))
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "野绿连空，天青垂水，素色溶漓都净。",
                style = TextStyle(fontSize = 14.sp, color = Color(0xFF5B8C2A))
            )
        }

        // Grid-like section
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(5) { index -> // Sample two categories (常用应用 and 查询应用)
                val iconsForCategory = if (index == 0) 6 else if (index == 1) 8 else if (index == 2) 1 else if (index == 3) 4 else 1// 动态控制每个类别下的图标数量
                val iconNames = if (index == 0) {
                    listOf("天气预报", "健康", "记账本", "记事本", "新闻", "日程管理") // 常用应用的图标名称
                }  else if(index == 1) {
                    listOf("计时器", "计算器", "日期计算器", "计分板","颜色转换器","随机数生成","3","二维码制作") // 查询应用的图标名称
                }else if(index == 2){
                    listOf("1")

                }else if (index == 3){
                    listOf("知识百科","词语近反义词","1","2")

                }else{
                    listOf("3")

                }

                val iconImages = if (index == 0) {
                    listOf(
                        R.drawable.iron_weather, R.drawable.iron_healthy, R.drawable.iron_accounts,
                        R.drawable.iron_record, R.drawable.iron_news, R.drawable.iron_data
                    ) // 对应的图片资源ID
                } else if (index == 1){
                    listOf(
                        R.drawable.iron_time, R.drawable.iron_calculate, R.drawable.iron_date_calculate, R.drawable.iron_scoreboard, R.drawable.iron_color, R.drawable.iron_suijishu, R.drawable.iron_decision,R.drawable.iron_decision
                    ) // 对应的图片资源ID
                }else if (index == 2){
                    listOf(R.drawable.iron_time,)

                }else if (index == 3){
                    listOf(
                        R.drawable.iron_time,R.drawable.iron_time,R.drawable.iron_time,R.drawable.iron_time,)

                }else{
                    listOf(R.drawable.iron_time,)

                }


                val targetActivities = if (index == 0) {
                    listOf(
                        WeatherForecastActivity::class.java,
                        HealthyActivity::class.java,
                        RecordActivity::class.java,
                        NoteActivity::class.java,
                        NewsActivity::class.java,
                        DataActivity::class.java
                    )
                } else if (index == 1) {
                    listOf(
                        TimerActivity::class.java,
                        ConterActivity::class.java,
                        DateActivity::class.java,
                        ScoreboardActivity::class.java,
                               ColorConverterActivity::class.java,
                               RandomNumberActivity::class.java,
                                LoginActivity::class.java,
                        QrCodeActivity::class.java
                    )
                }else if (index == 2){
                    listOf(
                        SudokuActivity::class.java,
                    )

                }else if (index == 3){
                    listOf(
                        QAActivity::class.java,
                        SynonymAntonymActivity::class.java,
                        MedicineInstructionActivity::class.java,
                        SynonymAntonymActivity::class.java
                    )
                }else{
                    listOf(
                        LoginActivity::class.java,
                    )
                }


                AppCategoryRow(
                    categoryName = if (index == 0) "常用应用" else if (index == 1) "实用应用" else if(index == 2) "娱乐游戏" else if(index == 3) "知识百科" else "其他",
                    iconNames = iconNames,
                    iconImages = iconImages,
                    targetActivities = targetActivities
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppCategoryRow(categoryName: String, iconNames: List<String>, iconImages: List<Int>, targetActivities: List<Class<*>>) {
    var isExpanded by remember { mutableStateOf(true) } // 管理展开/收起状态

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 8.dp)
                .clickable { isExpanded = !isExpanded }, // 点击切换展开状态
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = TextStyle(fontSize = 18.sp, color = Color.Gray)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown, // 根据状态切换箭头方向
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        // 展示或隐藏图标
        if (isExpanded) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 使用 FlowRow 使图标在一行显示不下时自动换行
                iconNames.forEachIndexed { index, iconName ->
                    AppIconButton(
                        iconName = iconName,
                        imageRes = iconImages[index],
                        targetActivity = targetActivities[index] // 为每个图标传递对应的 Activity
                    )
                }
            }
        }
    }
}


@Composable
fun AppIconButton(iconName: String, imageRes: Int, targetActivity: Class<*>?) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // 居中排列
        modifier = Modifier
            .padding(8.dp) // 给图标和文字周围留些空间
            .wrapContentSize() // 根据内容调整大小
            .clickable {
                targetActivity?.let {
                    // 启动新的 Activity
                    val intent = Intent(context, it)
                    context.startActivity(intent)
                }
            }
    ) {
        Card(
            modifier = Modifier
                .size(60.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFf4f4f4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes), // 动态加载对应的图片
                    contentDescription = null,
                    modifier = Modifier.size(24.dp) // 设置图片大小
                )
            }
        }
        // 图标下方显示名称
        Text(
            text = iconName,
            style = TextStyle(fontSize = 12.sp, color = Color.Black),
            modifier = Modifier.padding(top = 4.dp) // 添加顶部间距
        )
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppScreen()
}
