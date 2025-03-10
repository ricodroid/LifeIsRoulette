package com.ricodroid.roulettelife.presentation.rouletteSettings

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ricodroid.roulettelife.R
import com.ricodroid.roulettelife.data.local.RoulettePreferences

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RouletteWeekdaySettingsScreen(
    onHomeButtonClick: () -> Unit,
    onChangeWeekendButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // ルーレットのリスト項目を保持する状態
    var rouletteItems by remember {
        mutableStateOf(
            roulettePreferences.getWeekdayRouletteItems().toMutableList()
        )
    }
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
                    .background(Color(0xFFFFF9E6))
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.weekday_item),
                    style = MaterialTheme.typography.titleMedium
                )
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
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDC143C))
                                        .padding(10.dp),
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
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)  // アイテム自体の背景を白に設定
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.padding(start = 16.dp, top = 5.dp, bottom = 5.dp),
                                        text = item,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color =  Color(0xFF333333)
                                    )
                                    IconButton(onClick = {
                                        // 項目を削除して、ローカルリストとSharedPreferences を更新
                                        roulettePreferences.removeWeekdayRouletteItem(item)
                                        rouletteItems = rouletteItems.toMutableList().apply {
                                            removeAt(index)  // インデックスで削除
                                        }
                                    }) {
//                                        Icon(Icons.Default.Delete, contentDescription = "Delete Item")
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
                        title = { Text(text = stringResource(id = R.string.new_item)) },
                        text = {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.new_item_sub_title),
                                    fontSize = 16.sp  // 文字サイズを少し大きく設定
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BasicTextField(
                                    value = newItem,
                                    onValueChange = { newItem = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(8.dp)
                                        .background(Color.LightGray)
                                        .padding(16.dp),
                                    textStyle = TextStyle(fontSize = 16.sp)
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
                                Text(stringResource(id = R.string.add_item))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cardを使用してボタンを表示
                androidx.compose.material3.Card(
                    onClick = { onChangeWeekendButtonClick() }, // ここでCard自体をクリック可能に
                    modifier = Modifier
                        .width(200.dp)
                        .height(66.dp)
                        .padding(2.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFEF9) // 背景色を #FFFEF9 に変更
                    ),
                    elevation = CardDefaults.cardElevation(8.dp) // elevationの修正
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // アイコンを追加
                        Icon(
                            imageVector = Icons.Default.Refresh, // 好きなアイコンに変更可能
                            contentDescription = null,
                            tint = Color(0xFF007DC5), // アイコンの色
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp) // アイコンとテキストの間にスペースを追加
                        )
                        // テキストを追加
                        Text(
                            text = "Change Weekend",
                            color = Color(0xFF6D6D6D), // テキストの色
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}
