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
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
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
    // 从数据库加载数据
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            transactions = transactionDao.getAllTransactions().sortedByDescending { LocalDate.parse(it.date) } // 按日期排序
        }
    }

    // 根据搜索框筛选账单数据
    val filteredTransactions = transactions.filter {
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            // 搜索栏
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("搜索") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    // 新建按钮
                    IconButton(onClick = {
                        // 点击“新建”返回到记录界面
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
                // 遍历按日期分组的账单数据
                groupedTransactions.forEach { (date, transactionsForDate) ->
                    item {
                        if (transactionsForDate.isNotEmpty()) {
                            // 显示日期标题
                            Text(
                                text = "日期: $date",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // 展示对应日期的账单列表
                    items(transactionsForDate) { transaction ->
                        BillCard(transaction = transaction, onEditClicked = {
                            // 点击编辑，进入编辑页面
                            navController.navigate("edit_bill_screen/${transaction.id}")
                        })
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
