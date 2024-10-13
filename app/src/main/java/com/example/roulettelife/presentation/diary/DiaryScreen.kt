package com.example.roulettelife.presentation.diary

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.roulettelife.R
import com.example.roulettelife.data.local.DiaryPreferences
import com.example.roulettelife.data.local.RoulettePreferences

@Composable
fun DiaryScreen(
    photoUri: String,  // 渡された写真のURI
    diaryEntry: String,  // 渡された日記内容
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    val uri = Uri.parse(photoUri)
    val diaryPreferences = DiaryPreferences(context)
    val roulettePreferences = RoulettePreferences(context)
    var diaryText by remember { mutableStateOf(diaryEntry) }
    var showDeleteDialog by remember { mutableStateOf(false) } // 削除確認ダイアログの表示フラグ

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 写真の表示
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

        // 日記内容の表示
        Text(
            text = diaryEntry,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        // 日記を書けるエリア (編集可能なTextField)
        TextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            label = { Text("日記を入力してください") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(onClick = {
            // DiaryPreferencesを使用して日記をSharedPreferencesに保存
            diaryPreferences.saveDiary(photoUri, diaryText)

            // 削除確認ダイアログを表示
            showDeleteDialog = true
        }) {
            Text(text = "日記を保存")
        }

        // 削除確認ダイアログ
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("項目を削除しますか？") },
                text = { Text("$diaryEntry を削除しますか？") },
                confirmButton = {
                    Button(
                        onClick = {
                            // 削除処理: SharedPreferences または defaultItems から削除
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
                        Text("削除")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDeleteDialog = false
                        onRouletteButtonClick()
                    }) {
                        Text("残す")
                    }
                }
            )
        }
    }
}