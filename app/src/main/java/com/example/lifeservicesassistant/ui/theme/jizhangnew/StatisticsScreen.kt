package com.example.lifeservicesassistant.ui.theme.jizhangnew

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.SunnyWeatherApplication.Companion.context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.time.LocalDate
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val transactionDao = db.transactionDao()

    val currentMonth = LocalDate.now().withDayOfMonth(1)
    val endOfMonth = currentMonth.plusMonths(1).minusDays(1)

    // 统计金额
    var totalIncome by remember { mutableStateOf(0.0) }
    var totalExpense by remember { mutableStateOf(0.0) }
    var balance by remember { mutableStateOf(0.0) }
    var lastMonthBalance by remember { mutableStateOf(0.0) }

    // 统计每天的支出
    var dailyExpenses = remember { mutableStateOf<Map<LocalDate, Double>>(emptyMap()) }
    var dailyIncome = remember { mutableStateOf<Map<LocalDate, Double>>(emptyMap()) }
    var categoryExpenses by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var categoryIncome by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    var selectedCategoryType by remember { mutableStateOf("支出") }

    var expenseCategoryDetails by remember {
        mutableStateOf<Map<String, Pair<Double, Int>>>(emptyMap())
    }
    var incomeCategoryDetails by remember {
        mutableStateOf<Map<String, Pair<Double, Int>>>(emptyMap())
    }





    var categoryItems = listOf(
        CategoryItem("餐饮", R.drawable.ic_sport),
        CategoryItem("购物", R.drawable.ic_sport),
        CategoryItem("日用", R.drawable.ic_sport),
        CategoryItem("交通", R.drawable.ic_sport),
        CategoryItem("娱乐", R.drawable.ic_sport),
        CategoryItem("美容", R.drawable.ic_sport),
        CategoryItem("服饰", R.drawable.ic_sport),
        CategoryItem("水果", R.drawable.ic_sport),
        CategoryItem("蔬菜", R.drawable.ic_sport),
        CategoryItem("零食", R.drawable.ic_sport),
        CategoryItem("电子产品", R.drawable.ic_sport),
        CategoryItem("健康", R.drawable.ic_sport),
        CategoryItem("加号", R.drawable.iron_healthy)
    )

    var incomeCategoryItems = listOf(
        CategoryItem("工资", R.drawable.ic_sport),
        CategoryItem("红包", R.drawable.ic_sport),
        CategoryItem("租金", R.drawable.ic_sport),
        CategoryItem("礼金", R.drawable.ic_sport),
        CategoryItem("分红", R.drawable.ic_sport),
        CategoryItem("理财", R.drawable.ic_sport),
        CategoryItem("年终奖", R.drawable.ic_sport),
        CategoryItem("其他", R.drawable.ic_sport)
    )



    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // 获取本月的交易数据
            val allTransactions = transactionDao.getAllTransactions()
                .filter { transaction ->
                    // 解析数据库中的日期字符串为 LocalDate 类型
                    val transactionDate = LocalDate.parse(transaction.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    // 使用 ISO_LOCAL_DATE 格式进行比较
                    transactionDate.isAfter(currentMonth.minusDays(1)) && transactionDate.isBefore(endOfMonth.plusDays(1))
                }
            println("查询到的交易记录: $allTransactions")

            totalIncome = allTransactions.filter { it.amount > 0 }.sumOf { it.amount }
            totalExpense = allTransactions.filter { it.amount < 0 }.sumOf { it.amount }
            balance = totalIncome + totalExpense

            val expenseDetails = allTransactions
                .filter { it.amount < 0 }
                .groupBy { it.category }
                .mapValues { entry ->
                    Pair(
                        entry.value.sumOf { abs(it.amount) },
                        entry.value.size
                    )
                }
            println("Filtered Expense Details: $expenseDetails")
            expenseCategoryDetails = expenseDetails

            val incomeDetails = allTransactions
                .filter { it.amount > 0 }
                .groupBy { it.category }
                .mapValues { entry ->
                    Pair(
                        entry.value.sumOf { it.amount },
                        entry.value.size
                    )
                }
            println("Filtered Income Details: $incomeDetails")
            incomeCategoryDetails = incomeDetails

            println("支出类目详情: $expenseCategoryDetails")
            println("收入类目详情: $incomeCategoryDetails")

            // 计算每天的支出
            val dailyExpenseMap = allTransactions
                .filter { it.amount < 0 }
                .groupBy { LocalDate.parse(it.date) }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            dailyExpenses.value = dailyExpenseMap

            val dailyIncomeMap = allTransactions
                .filter { it.amount > 0 }
                .groupBy { LocalDate.parse(it.date) }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            dailyIncome.value = dailyIncomeMap


            val categoryMap = allTransactions
                .filter { it.amount < 0 }
                .groupBy { it.category }
                .mapValues { entry ->
                    entry.value.sumOf { abs(it.amount) }
                }

            categoryExpenses = categoryMap


            val incomeCategoryMap = allTransactions
                .filter { it.amount > 0 }  // 过滤收入
                .groupBy { it.category }
                .mapValues { entry ->
                    entry.value.sumOf { it.amount } // 收入已经是正数
                }

            categoryIncome = incomeCategoryMap


        /*    val categoryExpenseMap = allTransactions
                .filter { it.amount < 0 }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { abs(it.amount) } }
            categoryExpenses = categoryExpenseMap

            // 类别收入总额
            val categoryIncomeMap = allTransactions
                .filter { it.amount > 0 }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
            categoryIncome = categoryIncomeMap
*/

            // 获取上月的交易数据
            val lastMonthTransactions = transactionDao.getAllTransactions()
                .filter { transaction ->
                    val transactionDate = LocalDate.parse(transaction.date)
                    transactionDate.isAfter(currentMonth.minusMonths(1).minusDays(1)) && transactionDate.isBefore(currentMonth)
                }

            val lastMonthIncome = lastMonthTransactions.filter { it.amount > 0 }.sumOf { it.amount }
            val lastMonthExpense = lastMonthTransactions.filter { it.amount < 0 }.sumOf { it.amount }
            lastMonthBalance = lastMonthIncome + lastMonthExpense
        }
        println("Expense Category Details: $expenseCategoryDetails")
        println("Income Category Details: $incomeCategoryDetails")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("统计")
                },
                actions = {
                    IconButton(onClick = { navController.navigate("record_screen") }) {
                        Text("新建")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // 使得内容可滑动
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { selectedCategoryType = "支出" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategoryType == "支出") Color.Blue else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("支出")
                    }

                    Button(
                        onClick = { selectedCategoryType = "收入" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategoryType == "收入") Color.Blue else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("收入")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 统计信息容器
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 本月结余
                        Text(
                            text = "本月结余: ¥${"%.2f".format(balance)}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            color = if (balance >= 0) Color.Green else Color.Red
                        )

                        // 本月支出进度条
                        Text(
                            text = "本月支出: ¥${"%.2f".format(totalExpense)}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Gray
                        )

                        // 本月收入
                        Text(
                            text = "本月收入: ¥${"%.2f".format(totalIncome)}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Gray
                        )

                        // 显示进度条
                        Spacer(modifier = Modifier.height(16.dp))
                        val progress = if (totalIncome > 0) totalExpense / totalIncome else 0f
                        LinearProgressIndicator(
                            progress = progress.toFloat(),
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Yellow
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 上月结余
                        Text(
                            text = "上月结余: ¥${"%.2f".format(lastMonthBalance)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                if (selectedCategoryType == "支出") {
                    // 支出趋势图
                    Text("本月支出趋势", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

                    LineChartView(dailyExpenses = dailyExpenses.value, modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())

                    Spacer(modifier = Modifier.height(24.dp))

                    // 支出类别占比图
                    Text("支出类别占比", style = MaterialTheme.typography.bodyLarge)

                    PieChartView(categoryData = categoryExpenses, modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())
                } else {
                    // 收入趋势图
                    Text("本月收入趋势", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

                    LineChartView(dailyExpenses = dailyIncome.value, modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())

                    Spacer(modifier = Modifier.height(24.dp))

                    // 收入类别占比图
                    Text("收入类别占比", style = MaterialTheme.typography.bodyLarge)

                    PieChartView(categoryData = categoryIncome, modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (selectedCategoryType == "支出") "支出类目排行" else "收入类目排行",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                CategoryRankingList(
                    categories = if (selectedCategoryType == "支出")
                        expenseCategoryDetails else incomeCategoryDetails,
                    categoryItems = if (selectedCategoryType == "支出")
                        categoryItems else incomeCategoryItems,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}
/*                // 支出趋势图
                Text("本月支出趋势", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

                // 图表绘制部分
                LineChartView(dailyExpenses = dailyExpenses.value, modifier = Modifier
                    .height(300.dp) // 设置合适的高度
                    .fillMaxWidth())

                Spacer(modifier = Modifier.height(24.dp))

                Text("支出类别占比", style = MaterialTheme.typography.bodyLarge)

                PieChartView(
                    categoryData = categoryExpenses,
                    modifier = Modifier
                        .height(300.dp) // 设置合适的高度
                        .fillMaxWidth()
                )
            }
        }
    )
}*/


@Composable
fun LineChartView(dailyExpenses: Map<LocalDate, Double>, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                configureChartAppearance()
            }
        },
        update = { chart ->

            //val label = if (isIncome) "每日收入" else "每日支出"
            // 处理数据：按日期排序并转换绝对值
            val sortedEntries = dailyExpenses.entries
                .sortedBy { it.key }
                .map {
                    Entry(
                        it.key.dayOfMonth.toFloat(),  // X轴为日期
                        abs(it.value).toFloat()       // Y轴取正值
                    )
                }

            val dataSet = LineDataSet(sortedEntries, "每日支出").apply {
                color = ContextCompat.getColor(
                    context,
                    if (1==1) android.R.color.holo_green_dark
                    else android.R.color.holo_red_light
                )
                configureDataSetStyle(context)
            }

            chart.data = LineData(dataSet)

            // 配置X轴
            chart.xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString() + "日"
                    }
                }
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            // 配置Y轴
            chart.axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f  // 从0开始
            }
            chart.axisRight.isEnabled = false

            chart.invalidate() // 刷新图表
        }
    )
}

private fun LineChart.configureChartAppearance() {
    description.isEnabled = false
    setTouchEnabled(true)
    isDragEnabled = true
    setScaleEnabled(true)
    setPinchZoom(true)
    setNoDataText("暂无支出数据")
    legend.isEnabled = false

    // 设置动画
    animateXY(1000, 1000)
}

private fun LineDataSet.configureDataSetStyle(context: Context) {
    color = ContextCompat.getColor(context, android.R.color.holo_red_light)
    valueTextColor = Color.Black.toArgb()
    lineWidth = 2f
    mode = LineDataSet.Mode.CUBIC_BEZIER
    setDrawCircles(true)
    setDrawValues(true)
    setDrawFilled(true)
    fillColor = Color.Red.toArgb()
    fillAlpha = 50
}



@Composable
fun PieChartView(categoryData: Map<String, Double>, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                configurePieChartAppearance()
            }
        },
        update = { chart ->
            // 使用绝对值处理负数金额
            val entries = categoryData.map { (category, amount) ->
                PieEntry(abs(amount).toFloat(), category)  // 使用绝对值
            }

            val dataSet = PieDataSet(entries, "").apply {
                configurePieStyle(context)
            }

            chart.data = PieData(dataSet)

            // 设置百分比标签
            chart.setUsePercentValues(true)
            chart.invalidate()
        }
    )
}

private fun PieChart.configurePieChartAppearance() {
    description.isEnabled = false
    isDrawHoleEnabled = true
    holeRadius = 40f
    transparentCircleRadius = 45f
    setHoleColor(Color.Transparent.toArgb())
    setEntryLabelColor(Color.Black.toArgb())
    legend.isEnabled = true
    animateY(1000)
}

private fun PieDataSet.configurePieStyle(context: Context) {
    // 使用Material颜色
    val colors = listOf(
        ContextCompat.getColor(context, R.color.material_red_400),
        ContextCompat.getColor(context, R.color.material_blue_400),
        ContextCompat.getColor(context, R.color.material_green_400),
        ContextCompat.getColor(context, R.color.material_yellow_400),
        ContextCompat.getColor(context, R.color.material_purple_400),
        ContextCompat.getColor(context, R.color.purple_500)
    )
    this.colors = colors

    valueTextSize = 12f
    valueTextColor = Color.White.toArgb()

    // 设置百分比标签
    this.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "${value.toInt()}%" // 示例：显示"50元"
        }
    }
}





@Composable
private fun CategoryRankingList(
    categories: Map<String, Pair<Double, Int>>, // 传入类别和金额、笔数
    categoryItems: List<CategoryItem>,          // 所有分类
    modifier: Modifier = Modifier

) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 按金额排序取前10
            val sortedCategories = categories.entries
                .sortedByDescending { it.value.first }
                .take(10)

            if (sortedCategories.isEmpty()) {
                Text("暂无数据", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                sortedCategories.forEachIndexed { index, (category, data) ->
                    // 根据类别名称查找对应的图标资源
                    val iconRes = categoryItems.firstOrNull { it.name == category }?.iconRes
                        ?: R.drawable.ic_sport  // 默认图标

                    // 渲染行
                    println("Received Expense Categories: $categories")
                    println("Received Income Categories: $categories")
                    CategoryItemRow(
                        category = category,
                        amount = data.first,
                        count = data.second,
                        iconRes = iconRes,
                        rank = index + 1
                    )
                    if (index != sortedCategories.lastIndex) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun CategoryItemRow(
    category: String,
    amount: Double,
    count: Int,
    iconRes: Int,
    rank: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 排名标识
            Text(
                text = "$rank.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.width(32.dp)
            )

            // 图标
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 类目名称
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        // 金额和笔数
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "¥${"%.1f".format(amount)}", // 显示金额
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = "${count}笔", // 显示笔数
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}
