package com.example.lifeservicesassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifeservicesassistant.ui.theme.jizhangnew.DetailsScreen
import com.example.lifeservicesassistant.ui.theme.jizhangnew.EditBillScreen
import com.example.lifeservicesassistant.ui.theme.jizhangnew.ExpenseTrackerScreen
import com.example.lifeservicesassistant.ui.theme.jizhangnew.StatisticsScreen


class RecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                // 基础布局容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerApp()
                }
            }
        }
    }
}

@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()

    // 使用 Scaffold 来放置底部导航栏，并设置页面内容
    Scaffold(
        bottomBar = {
            NavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // 使用 NavHost 管理页面跳转和导航
        NavHost(
            navController = navController,
            startDestination = "record_screen",
            modifier = Modifier.padding(innerPadding) // 确保页面内容不会被导航栏覆盖
        ) {
            composable("record_screen") { ExpenseTrackerScreen(navController = navController) }
            composable("details_screen") { DetailsScreen(navController = navController) }
            composable("statistics_screen") { StatisticsScreen(navController = navController) }
            composable("edit_bill_screen/{billId}") { backStackEntry ->
                val billId = backStackEntry.arguments?.getString("billId")?.toLongOrNull()
                EditBillScreen(billId = billId, navController = navController)
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavController) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "明细") },
            label = { Text("明细") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "details_screen",
            onClick = {
                navController.navigate("details_screen") {
                    // Avoid multiple stack entries
                    popUpTo = navController.graph.startDestinationId
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "记录") },
            label = { Text("记录") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "record_screen",
            onClick = {
                navController.navigate("record_screen") {
                    popUpTo = navController.graph.startDestinationId
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.PieChart, contentDescription = "统计") },
            label = { Text("统计") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "statistics_screen",
            onClick = {
                navController.navigate("statistics_screen") {
                    popUpTo = navController.graph.startDestinationId
                    launchSingleTop = true
                }
            }
        )
    }
}