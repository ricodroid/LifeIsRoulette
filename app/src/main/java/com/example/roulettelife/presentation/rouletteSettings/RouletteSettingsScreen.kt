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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteSettingsScreen() {
    // ルーレットのリスト項目を保持する状態
    // このrouletteItemsは、いつかはAPI化するけど最初のうちは端末内に保存する
    var rouletteItems by remember { mutableStateOf(listOf("踊る", "無駄な買い物をする", "猫の写真を取る")) }
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
                                        rouletteItems = rouletteItems + newItem
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
