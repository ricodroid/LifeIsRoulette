package com.example.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.data.local.DiaryPreferences


@Composable
fun DiaryDetailScreen(
    navController: NavController,
    photoUri: String,
    context: Context = LocalContext.current
) {
    // DiaryPreferencesを使って、写真に対応する日記を取得
    val diaryPreferences = DiaryPreferences(context)
    val savedDiaryEntry = diaryPreferences.getDiary(photoUri) ?: ""

    // 日記の状態を管理
    var diaryText by remember { mutableStateOf(savedDiaryEntry) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 写真の表示
        if (photoUri.isNotEmpty()) {
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = "Diary Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )
        }

        // 日記を書けるエリア（編集可能なTextField）
        TextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            label = { Text("日記を入力してください") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 日記を保存するボタン
        Button(
            onClick = {
                // DiaryPreferencesに日記を保存
                diaryPreferences.saveDiary(photoUri, diaryText)
                // 前の画面に戻る
                navController.popBackStack()
            }
        ) {
            Text(text = "日記を保存")
        }
    }
}