import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.example.lifeservicesassistant.ui.theme.healthy.MedicineItem
import com.example.lifeservicesassistant.ui.theme.healthy.MedicineResponse
import com.example.lifeservicesassistant.ui.theme.healthy.MedicineViewModel

@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel,
    apiKey: String,
    onBackClick: () -> Unit = {}
) {
    var keyword by remember { mutableStateOf("") }
    val result by viewModel.medicineResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // 搜索栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "返回")
            }

            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("输入中药名称") },
                trailingIcon = {
                    if (keyword.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchMedicine(apiKey, keyword) }) {
                            Icon(Icons.Default.Search, "搜索")
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 错误提示
        error?.let { message ->
            ErrorMessage(message)
        }

        // 内容区域
        when {
            isLoading -> LoadingIndicator()
            result != null -> MedicineContent(result!!)
            else -> EmptyPlaceholder()
        }
    }
}

@Composable
private fun MedicineContent(response: MedicineResponse) {
    val medicineList = response.result?.list ?: emptyList()

    if (medicineList.isEmpty()) {
        EmptyPlaceholder()
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(medicineList) { item ->
                MedicineItemCard(item)
            }
        }
    }
}

@Composable
private fun MedicineItemCard(item: MedicineItem) {
    Card(
        elevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.h6,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = HtmlCompat.fromHtml(
                    item.content
                        .replace("<p>", "\n")
                        .replace("</p>", "")
                        .replace("<br>", "\n"),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString(),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, MaterialTheme.colors.error),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colors.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "未找到相关药材信息",
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}