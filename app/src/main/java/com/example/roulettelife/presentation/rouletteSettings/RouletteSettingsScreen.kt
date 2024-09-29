package com.example.roulettelife.presentation.rouletteSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roulettelife.data.local.RoulettePreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteSettingsScreen() {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }
    // ルーレットのリスト項目を保持する状態
    // このrouletteItemsは、いつかはAPI化するけど最初のうちは端末内に保存する
    // SharedPreferences から初期値を取得
    var rouletteItems by remember { mutableStateOf(roulettePreferences.getRouletteItems()) }
    var newItem by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ルーレットに追加された項目", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // ルーレットのリストを表示
                rouletteItems.forEach { item ->
                    Text(text = item, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 項目追加用のダイアログ
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = "新しい項目を追加") },
                        text = {
                            Column {
                                Text("ルーレットに追加する項目を入力してください")
                                Spacer(modifier = Modifier.height(8.dp))
                                BasicTextField(
                                    value = newItem,
                                    onValueChange = { newItem = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(Color.LightGray)
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (newItem.isNotBlank()) {
                                        // 新しいアイテムを追加し、SharedPreferences に保存
                                        roulettePreferences.addRouletteItem(newItem)
                                        rouletteItems = roulettePreferences.getRouletteItems()  // リストを更新
                                        newItem = ""  // 入力フィールドをクリア
                                        showDialog = false
                                    }
                                }
                            ) {
                                Text("追加")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("キャンセル")
                            }
                        }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewRouletteSettingsScreen() {
    RouletteSettingsScreen()
}
