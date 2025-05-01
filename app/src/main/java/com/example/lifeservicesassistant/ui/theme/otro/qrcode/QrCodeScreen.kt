package com.example.lifeservicesassistant.ui.theme.otro.qrcode// QrCodeScreen.kt
import android.content.Context
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    viewModel: QrCodeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showHistory by remember { mutableStateOf(false) }

    // 错误处理
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
                viewModel.uiState = viewModel.uiState.copy(error = null)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 输入区域
                var text by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("输入二维码内容") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 高级设置
                AdvancedSettings(
                    uiState = uiState,
                    onSettingsChanged = { el, bgColor, fgColor, logoUrl, size, margin, logoWidth ->
                        viewModel.updateSettings(el, bgColor, fgColor, logoUrl, size, margin, logoWidth)
                    }
                )

                // 生成按钮
                Button(
                    onClick = { viewModel.generateQrCode(text) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = text.isNotEmpty() && !uiState.isLoading
                ) {
                    Text("生成二维码")
                }

                // 加载状态
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // 显示二维码
                uiState.base64Image?.let { base64 ->
                    QrCodeImage(
                        base64Image = base64,
                        onSaveClicked = {
                            viewModel.saveQrCodeToGallery(context, base64)
                        }
                    )
                }

                // 历史记录按钮
                Button(
                    onClick = { showHistory = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("查看生成历史")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 历史记录弹窗
    if (showHistory) {
        HistoryDialog(
            historyList = viewModel.historyList,
            onDismiss = { showHistory = false },
            onItemClick = { base64 ->
                viewModel.uiState = viewModel.uiState.copy(base64Image = base64)
                showHistory = false
            }
        )
    }
}

@Composable
fun QrCodeImage(
    base64Image: String?,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(base64Image) {
        base64Image?.let { base64 ->
            try {
                val cleanBase64 = base64.replace("\n", "").trim()
                val bytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                Log.e("QRCode", "解码失败", e)
                null
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = modifier
                    .size(200.dp)
                    .clickable(onClick = onSaveClicked)
            )
            Text(
                text = "点击保存到相册",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text("无法加载二维码", color = Color.Red)
        }
    }
}

@Composable
fun HistoryDialog(
    historyList: List<QrHistory>,
    onDismiss: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = "生成历史",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (historyList.isEmpty()) {
                    Text(
                        text = "暂无历史记录",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { item ->
                            QrHistoryItem(item) {  // 修改组件名为 QrHistoryItem
                                onItemClick(item.base64Image)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrHistoryItem(  // 重命名组件避免冲突
    item: QrHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)  // 修复括号问题
        ) {  // 添加闭合括号
            QrCodeThumbnail(base64Image = item.base64Image)
            Column {
                Text(
                    text = item.content.take(20) + if (item.content.length > 20) "..." else "",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = DateFormat.format("yyyy-MM-dd HH:mm", Date(item.timestamp)).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun QrCodeThumbnail(base64Image: String) {
    val bitmap = remember(base64Image) {
        try {
            val bytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "缩略图",
            modifier = Modifier.size(50.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray)
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettings(
    uiState: QrCodeUiState,
    onSettingsChanged: (
        el: String?,
        bgColor: String?,
        fgColor: String?,
        logoUrl: String?,
        size: Int?,
        margin: Int?,
        logoWidth: Int?
    ) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (expanded) "隐藏高级设置" else "显示高级设置")
        }
        
        if (expanded) {
            var el by remember { mutableStateOf(uiState.el ?: "") }
            var bgColor by remember { mutableStateOf(uiState.bgColor ?: "") }
            var fgColor by remember { mutableStateOf(uiState.fgColor ?: "") }
            var logoUrl by remember { mutableStateOf(uiState.logoUrl ?: "") }
            var size by remember { mutableStateOf(uiState.size?.toString() ?: "") }
            var margin by remember { mutableStateOf(uiState.margin?.toString() ?: "") }
            var logoWidth by remember { mutableStateOf(uiState.logoWidth?.toString() ?: "") }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = el,
                    onValueChange = {
                        el = it
                        onSettingsChanged(
                            it.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("纠错等级 (h/q/m/l)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = bgColor,
                    onValueChange = {
                        bgColor = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("背景色 (FFFFFF)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = fgColor,
                    onValueChange = {
                        fgColor = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("前景色 (000000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = logoUrl,
                    onValueChange = {
                        logoUrl = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("Logo URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = size,
                    onValueChange = {
                        size = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            it.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("尺寸大小 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = margin,
                    onValueChange = {
                        margin = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            it.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("边距大小 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = logoWidth,
                    onValueChange = {
                        logoWidth = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            it.toIntOrNull()
                        )
                    },
                    label = { Text("Logo宽度 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}