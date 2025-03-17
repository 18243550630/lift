package com.example.lifeservicesassistant.ui.theme.jizhangnew

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifeservicesassistant.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExpenseTrackerScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val transactionDao = db.transactionDao()
    val sharedPreferences = context.getSharedPreferences("expense_tracker", Context.MODE_PRIVATE)
    val categoryItemsJson = sharedPreferences.getString("category_items", "[]")
    val transactionListJson = sharedPreferences.getString("transactions", "[]")

    // 数据状态
    var selectedCategoryType by remember { mutableStateOf("支出") }
    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var amount by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // 分类数据
    var categoryItems = listOf(
        CategoryItem("餐饮", R.drawable.ic_food),
        CategoryItem("购物", R.drawable.ic_shop),
        CategoryItem("日用", R.drawable.ic_dayuse),
        CategoryItem("交通", R.drawable.ic_tran),
        CategoryItem("娱乐", R.drawable.ic_play),
        CategoryItem("美容", R.drawable.ic_meirong),
        CategoryItem("服饰", R.drawable.ic_cloths),
        CategoryItem("水果", R.drawable.ic_fruit),
        CategoryItem("蔬菜", R.drawable.ic_vertable),
        CategoryItem("零食", R.drawable.ic_lingshi),
        CategoryItem("电子产品", R.drawable.ic_dianzi),
        CategoryItem("健康", R.drawable.ic_healthy),
        CategoryItem("其他", R.drawable.ic_other),
        CategoryItem("添加", R.drawable.ic_add)
    )

    var incomeCategoryItems = listOf(
        CategoryItem("工资", R.drawable.ic_money),
        CategoryItem("红包", R.drawable.ic_hongbao),
        CategoryItem("租金", R.drawable.ic_zujin),
        CategoryItem("礼金", R.drawable.ic_lijin),
        CategoryItem("分红", R.drawable.ic_fenhong),
        CategoryItem("理财", R.drawable.ic_licai),
        CategoryItem("年终奖", R.drawable.ic_nianzhongjiang),
        CategoryItem("其他", R.drawable.ic_other)
    )



    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 切换按钮（支出/收入）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { selectedCategoryType = "支出" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategoryType == "支出") Color.Blue else Color.Gray
                )
            ) {
                Text("支出")
            }
            Button(
                onClick = { selectedCategoryType = "收入" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategoryType == "收入") Color.Blue else Color.Gray
                )
            ) {
                Text("收入")
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // HorizontalPager 用于分页显示每一页
        HorizontalPager(
            state = pagerState,
            count = (if (selectedCategoryType == "支出") categoryItems.size else incomeCategoryItems.size) / 12 + 1,
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { pageIndex ->
            val currentCategoryItems = if (selectedCategoryType == "支出") categoryItems else incomeCategoryItems

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(currentCategoryItems.chunked(12).getOrElse(pageIndex) { emptyList() }) { categoryItem ->
                    CategoryButtonWithIconAndText(
                        categoryItem = categoryItem,
                        isSelected = selectedCategory == categoryItem,
                        onClick = {
                            selectedCategory = if (selectedCategory == categoryItem) null else categoryItem
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 输入金额部分
        TextField(
            value = amount,
            onValueChange = {
                if (it.toDoubleOrNull() != null || it.isEmpty()) {
                    amount = it
                }
            },
            label = { Text("输入金额") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = remark,
            onValueChange = { remark = it },
            label = { Text("输入备注") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 显示日期
        Text("日期: ${date.format(DateTimeFormatter.ISO_DATE)}", style = MaterialTheme.typography.bodySmall)
        Button(onClick = { showDatePicker = true }) {
            Text("选择日期")
        }

        // 日期选择弹窗
        if (showDatePicker) {
            DatePickerDialog(onDateSelected = { selectedDate ->
                date = selectedDate
                showDatePicker = false
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 完成按钮，保存数据
        Button(
            onClick = {
                if (selectedCategory != null && amount.isNotEmpty() && remark.isNotEmpty()) {
                    val iconResId = selectedCategory!!.iconRes
                    val transaction = Transaction(
                        category = selectedCategory!!.name,
                        amount = if (selectedCategoryType == "支出") -amount.toDouble() else amount.toDouble(),
                        remark = remark,
                        date = date.toString(),
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        transactionDao.insertTransaction(transaction)
                    }
                    navController.navigate("details_screen")
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("完成")
        }
    }

    // 添加新分类的对话框
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("添加新分类") },
            text = {
                Column {
                    TextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("请输入分类名称") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotEmpty()) {
                            val newCategory = CategoryItem(newCategoryName, R.drawable.ic_sport)
                            if (selectedCategoryType == "支出") {
                                categoryItems = categoryItems.toMutableList().apply { add(newCategory) }
                            } else {
                                incomeCategoryItems = incomeCategoryItems.toMutableList().apply { add(newCategory) }
                            }
                            val updatedJson = Gson().toJson(if (selectedCategoryType == "支出") categoryItems else incomeCategoryItems)
                            sharedPreferences.edit().putString("category_items", updatedJson).apply()
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                Button(onClick = { showAddCategoryDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun CategoryButtonWithIconAndText(categoryItem: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clickable { onClick() }
    ) {
        // 卡片样式的图标
        Card(
            modifier = Modifier
                .size(60.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.LightGray else Color(0xFFf4f4f4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = categoryItem.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 图标下方显示名称
        Text(
            text = categoryItem.name,
            style = TextStyle(fontSize = 12.sp, color = Color.Black),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

data class CategoryItem(val name: String, val iconRes: Int)



@Composable
fun DatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
    // 使用 DatePicker 实现日期选择器，这里可以根据实际需求自定义实现
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(context)
    datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
        onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
    }
    datePickerDialog.show()
}








/*
@Composable
fun NavigationBar(navController: NavController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "明细") },
            label = { Text("明细") },
            selected = false,  // 根据需要更新选中状态
            onClick = { */
/* Navigation logic *//*
 }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "记录") },
            label = { Text("记录") },
            selected = true,  // 根据需要更新选中状态
            onClick = { */
/* Navigation logic *//*
 }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.PieChart, contentDescription = "统计") },
            label = { Text("统计") },
            selected = false,  // 根据需要更新选中状态
            onClick = { */
/* Navigation logic *//*
 }
        )
    }
}*/
