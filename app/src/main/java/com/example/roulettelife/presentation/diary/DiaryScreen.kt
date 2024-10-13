package com.example.roulettelife.presentation.diary

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.data.local.DiaryPreferences

@Composable
fun DiaryScreen(
    photoUri: String,  // 渡された写真のURI
    diaryEntry: String,  // 渡された日記内容
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    val uri = Uri.parse(photoUri)
    val diaryPreferences = DiaryPreferences(context)

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
        var diaryText by remember { mutableStateOf(diaryEntry) }
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

            // ルーレット画面に遷移する
            onRouletteButtonClick()
        }) {
            Text(text = "日記を保存")
        }
    }
}