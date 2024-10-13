package com.example.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.R
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

    // 編集モードの状態を管理
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),  // スクロール可能にする
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 写真の表示（全体を表示できるようにする）
            if (photoUri.isNotEmpty()) {
                AsyncImage(
                    model = Uri.parse(photoUri),
                    contentDescription = stringResource(id = R.string.diary_photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)  // アスペクト比を維持しつつ全体表示
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit  // 全体表示
                )
            }

            // 日記を書けるエリア
            TextField(
                value = diaryText,
                onValueChange = { diaryText = it },
                label = { Text(stringResource(id = R.string.please_enter_diary)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // 高さを固定
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState()),
                enabled = isEditing,  // 編集モードがオンの場合のみ編集可能
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black
                )
            )
        }

        // 右下に丸い編集/完了ボタン
        FloatingActionButton(
            onClick = {
                if (isEditing) {
                    // 編集モードの場合、完了を押したら保存
                    diaryPreferences.saveDiary(photoUri, diaryText)
                }
                isEditing = !isEditing  // 編集モードの切り替え
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFE57373)  // ボタンの背景色
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,  // 編集中かどうかでアイコンを切り替え
                contentDescription = if (isEditing) stringResource(id = R.string.done_editing) else stringResource(id = R.string.edit),
                tint = Color.White  // アイコンの色を白に設定
            )
        }
    }
}
