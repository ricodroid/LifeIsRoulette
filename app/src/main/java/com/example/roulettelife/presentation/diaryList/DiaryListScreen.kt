package com.example.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.data.local.DiaryPreferences
import com.example.roulettelife.presentation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    navController: NavController,
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    // DiaryPreferencesを使用して保存された日記の一覧を取得
    val diaryPreferences = DiaryPreferences(context)
    val diaryEntries = diaryPreferences.getAllDiaries()
    Log.d("diaryEntries", "diaryEntries=$diaryEntries")

    // 日記のURIと内容をリスト形式に変換
    val diaryList = diaryEntries.entries.toList()  // List<Map.Entry<String, String>>
    Log.d("diaryList", "diaryList=$diaryList")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diary List") },
                actions = {
                    IconButton(onClick = { onRouletteButtonClick() }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        content = { padding ->
            // LazyColumnで日記の一覧を表示
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Listのサイズを取得して items に渡す
                items(diaryList.size) { index ->
                    val entry = diaryList[index]
                    DiaryListItem(
                        photoUri = entry.key,   // key は photoUri
                        diaryEntry = entry.value, // value は diaryEntry
                        onClick = {
                            // 項目をクリックした時のアクション (詳細画面へ遷移する例)
                            navController.navigate(Screens.DIARY_DETAIL.createRoute(entry.key))
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun DiaryListItem(
    photoUri: String,
    diaryEntry: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Start
    ) {
        // 写真を表示 (写真のURIが有効なら)
        if (photoUri.isNotEmpty()) {
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = "Diary Photo",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
        }

        // 日記の一部を表示
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = diaryEntry,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis  // テキストが長すぎる場合に省略
            )
        }
    }
}