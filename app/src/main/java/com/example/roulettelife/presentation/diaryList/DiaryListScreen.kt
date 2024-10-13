package com.example.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.R
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
                title = { Text(stringResource(id = R.string.diary)) },
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
                    val diaryEntryWithDate = entry.value.split("\n") // 日記と日付を分離
                    val diaryText = diaryEntryWithDate[0] // 日記の内容
                    val diaryDate = if (diaryEntryWithDate.size > 1) diaryEntryWithDate[1] else stringResource(
                        id = R.string.not_available
                    ) // 日付

                    DiaryListItem(
                        photoUri = entry.key,   // key は photoUri
                        diaryEntry = diaryText, // value は日記
                        diaryDate = diaryDate, // 日付を追加
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
    diaryDate: String,  // 日付を表示するための引数を追加
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
                contentDescription = stringResource(id = R.string.diary_photo),
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
        }

        // 日記の一部と日付を表示
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = diaryDate,  // 日付を表示
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
