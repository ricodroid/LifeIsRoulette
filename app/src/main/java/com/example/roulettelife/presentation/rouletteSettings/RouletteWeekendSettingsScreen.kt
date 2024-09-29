package com.example.roulettelife.presentation.rouletteSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.roulettelife.data.local.RoulettePreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteWeekendSettingsScreen(
    onHomeButtonClick: () -> Unit,
    onChangeWeekendButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // ルーレットのリスト項目を保持する状態
    var rouletteItems by remember { mutableStateOf(roulettePreferences.getWeekendRouletteItems()) }
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
                Text(text = "休日", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // スクロール可能なリスト
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // items(rouletteItems.size)を使ってListのサイズを渡す
                    items(rouletteItems.size) { index ->
                        val item = rouletteItems[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // bodyMedium -> bodyLarge などに変更
                            Text(text = item, style = MaterialTheme.typography.bodyLarge)

                            IconButton(onClick = {
                                // 項目を削除して、SharedPreferences を更新
                                roulettePreferences.removeWeekendRouletteItem(item)
                                rouletteItems = roulettePreferences.getWeekendRouletteItems().toList()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Item")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                                        roulettePreferences.saveWeekendRouletteItems(newItem)
                                        rouletteItems = roulettePreferences.getWeekendRouletteItems().toList()  // リストを更新して、List<String>を確保
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

                Spacer(modifier = Modifier.height(16.dp))

                // 設定画面に移動するボタン
                Button(onClick = { onHomeButtonClick() }) {
                    Text(text = "Go to Roulette")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onChangeWeekendButtonClick() }) {
                    Text(text = "change！")
                }
            }
        }
    )
}
