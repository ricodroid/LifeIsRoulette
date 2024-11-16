package com.ricodroid.roulettelife.presentation.diary

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ricodroid.roulettelife.R
import com.ricodroid.roulettelife.data.local.DiaryPreferences
import com.ricodroid.roulettelife.data.local.RoulettePreferences

@Composable
fun DiaryScreen(
    photoUri: String,
    diaryEntry: String,
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    val uri = Uri.parse(photoUri)
    val diaryPreferences = DiaryPreferences(context)
    val roulettePreferences = RoulettePreferences(context)
    var diaryText by remember { mutableStateOf(diaryEntry) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F4))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Saved photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            }
        }

        item {
            Text(
                text = diaryEntry,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp),
                color = Color(0xFF333333)
            )
        }

        item {
            TextField(
                value = diaryText,
                onValueChange = { diaryText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFFF9E6), // フォーカス時の背景色
                    unfocusedContainerColor = Color(0xFFFFF9E6) // 非フォーカス時の背景色
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF333333) // テキストの色を設定
                )
            )
        }

        item {
            // 背景の設定
            Box(
                modifier = Modifier
                    .size(48.dp)  // サイズを指定
                    .clip(CircleShape)  // 丸くする
                    .background(Color(0xFF3155A6))
            ) {
                // アイコンボタン
                IconButton(
                    onClick = {
                        // DiaryPreferencesを使用して日記をSharedPreferencesに保存
                        diaryPreferences.saveDiary(photoUri, diaryText)
                        showDeleteDialog = true

                        // SNSに共有するためのインテントを作成
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, diaryText)
                            type = "text/plain"
                            // 画像も一緒に共有する場合
                            if (photoUri.isNotEmpty()) {
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "image/*"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                        }

                        // シェアメニューを表示
                        val shareIntent = Intent.createChooser(sendIntent, "Share your diary")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxSize()  // ボタン全体に広げる
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,  // Doneアイコンを使用
                        contentDescription = "Save",  // アイコンの説明
                        tint = Color.White  // アイコンの色を白に設定
                    )
                }
            }
        }
    }

    // 削除確認ダイアログ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.delete_item)) },
            text = { Text(stringResource(id = R.string.delete_item_detail)) },
            confirmButton = {
                Button(
                    onClick = {
                        // 削除処理
                        if (diaryEntry in context.resources.getStringArray(R.array.default_weed_day_roulette_items)) {
                            roulettePreferences.saveDeletedDefaultItem(diaryEntry)
                        } else {
                            roulettePreferences.removeWeekdayRouletteItem(diaryEntry)
                        }
                        showDeleteDialog = false
                        // ルーレット画面に遷移
                        onRouletteButtonClick()
                    }
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    onRouletteButtonClick()
                }) {
                    Text(stringResource(id = R.string.keep))
                }
            }
        )
    }
}