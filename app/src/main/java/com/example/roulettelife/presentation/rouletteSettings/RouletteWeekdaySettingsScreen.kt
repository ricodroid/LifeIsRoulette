package com.example.roulettelife.presentation.rouletteSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.roulettelife.R
import com.example.roulettelife.data.local.RoulettePreferences

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RouletteWeekdaySettingsScreen(
    onHomeButtonClick: () -> Unit,
    onChangeWeekendButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // ルーレットのリスト項目を保持する状態
    var rouletteItems by remember { mutableStateOf(roulettePreferences.getWeekdayRouletteItems().toMutableList()) }
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
                Text(text = stringResource(id = R.string.weekday_item), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // スクロール可能なリスト
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(rouletteItems.size, key = { index -> rouletteItems[index] }) { index ->
                        val item = rouletteItems[index]
                        val dismissState = rememberDismissState()

                        SwipeToDismiss(
                            state = dismissState,
                            background = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Item",
                                        tint = Color.White
                                    )
                                }
                            },
                            directions = setOf(DismissDirection.EndToStart),
                            dismissContent = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = item, style = MaterialTheme.typography.bodyLarge)

                                    IconButton(onClick = {
                                        // 項目を削除して、SharedPreferences を更新
                                        roulettePreferences.removeWeekdayRouletteItem(item)
                                        rouletteItems = rouletteItems.toMutableList().apply {
                                            removeAt(index)  // インデックスで削除
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Item")
                                    }
                                }
                            }
                        )

                        // スワイプでアイテムが削除された場合の処理
                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            rouletteItems = rouletteItems.toMutableList().apply {
                                removeAt(index)  // インデックスで削除
                            }
                            roulettePreferences.removeWeekdayRouletteItem(item)
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
                                        roulettePreferences.saveWeekdayRouletteItems(newItem)
                                        rouletteItems = rouletteItems.toMutableList().apply {
                                            add(newItem)  // 新しいアイテムをリストに追加
                                        }
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

                Button(onClick = { onChangeWeekendButtonClick() }) {
                    Text(text = "change！")
                }
            }
        }
    )
}
