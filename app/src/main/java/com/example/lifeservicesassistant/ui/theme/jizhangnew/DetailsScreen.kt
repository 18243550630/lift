package com.example.lifeservicesassistant.ui.theme.jizhangnew

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import com.example.lifeservicesassistant.R
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val transactionDao = db.transactionDao()

    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val groupedTransactions = transactions.groupBy { it.date }
    val backStackEntry by navController.currentBackStackEntryAsState()
    // 从数据库加载数据
    LaunchedEffect(backStackEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            // 从数据库加载最新数据（包含任何新增/修改的记录）
            val latestData = transactionDao.getAllTransactions()
                .sortedByDescending { LocalDate.parse(it.date) }

            // 切换到主线程更新界面
            withContext(Dispatchers.Main) {
                transactions = latestData
            }
        }
    }

    // 根据搜索框筛选账单数据
    val filteredTransactions = remember(searchQuery, transactions) {
        transactions.filter {
            it.category.contains(searchQuery, ignoreCase = true) ||
                    it.remark.contains(searchQuery, ignoreCase = true) ||
                    "%.2f".format(it.amount).contains(searchQuery)
        }
    }

    val groupedEntries by remember(filteredTransactions) {
        derivedStateOf {
            filteredTransactions
                .groupBy { it.date }
                .toList()
                .sortedByDescending { (dateStr, _) ->
                    LocalDate.parse(dateStr)
                }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it }, // 输入时实时更新状态
                        label = { Text("支持分类/备注/金额搜索") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("record_screen")
                    }) {
                        Text("新建")
                    }
                }
            )
        },
        content = { padding ->
            // 使用 LazyColumn 包裹所有内容，使得所有内容都可以滑动
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (groupedEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("没有找到匹配的账单记录")
                        }
                    }
                }else {
                    groupedEntries.forEach { (date, transactionsForDate) ->
                        item {
                            Text(
                                text = "日期: $date",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(
                            items = transactionsForDate,
                            key = { it.id } // 为每个账单项设置唯一键
                        ) { transaction ->
                            BillCard(
                                transaction = transaction,
                                onEditClicked = {
                                    navController.navigate(
                                        "edit_bill_screen/${transaction.id}"
                                    )
                                }
                            )
                        }
                    }
                }

            }
        }
    )
}
                @Composable
fun BillCard(transaction: Transaction, onEditClicked: () -> Unit) {
    // 每个账单项显示为卡片布局
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEditClicked() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 分类和金额显示
            Column {
                Text(transaction.category, style = MaterialTheme.typography.bodySmall)
                Text("金额: ¥${transaction.amount}", style = MaterialTheme.typography.bodySmall)
            }
            // 更多操作图标（如编辑）
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Filled.Edit, contentDescription = "编辑")
            }
        }
    }
}

@Composable
fun EditBillScreen(billId: Long?, navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val transactionDao = db.transactionDao()

    // 定义账单的状态变量
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(0.0) }
    var remark by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }

    // 在协程中加载账单数据
    LaunchedEffect(billId) {
        billId?.let {
            val transaction = transactionDao.getTransactionById(it)
            transaction?.let {
                category = it.category
                amount = it.amount
                remark = it.remark
                date = LocalDate.parse(it.date) // 根据保存的 ISO 日期格式进行转换
            }
        }
    }

    // 保存修改后的账单数据
    fun saveChanges() {

        val updatedTransaction = Transaction(
            id = billId ?: 0,
            category = category,
            amount = amount,
            remark = remark,
            date = date.toString() ,// 保持日期格式一致
        )
        CoroutineScope(Dispatchers.IO).launch {
            if (billId != null) {
                transactionDao.updateTransaction(updatedTransaction) // 更新数据库
            } else {
                transactionDao.insertTransaction(updatedTransaction) // 插入新账单（如果没有id）
            }
        }
        navController.popBackStack() // 返回明细页
    }



    // 删除账单
    fun deleteBill() {
        CoroutineScope(Dispatchers.IO).launch {
            billId?.let {
                transactionDao.deleteTransactionById(it)
            }
        }
        navController.popBackStack() // 返回明细页
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // 显示账单信息
        Text("编辑账单", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 分类输入框
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("分类") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 金额输入框
        TextField(
            value = amount.toString(),
            onValueChange = { amount = it.toDoubleOrNull() ?: 0.0 },
            label = { Text("金额") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 备注输入框
        TextField(
            value = remark,
            onValueChange = { remark = it },
            label = { Text("备注") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 显示日期
        Text("日期: ${date.format(DateTimeFormatter.ISO_DATE)}", style = MaterialTheme.typography.bodySmall)

        // 编辑日期的按钮
        Button(onClick = { /* 显示日期选择器 */ }) {
            Text("修改日期")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 操作按钮：保存和删除
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { saveChanges() },
                modifier = Modifier.weight(1f)
            ) {
                Text("保存")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { deleteBill() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("删除")
            }
        }
    }
}
