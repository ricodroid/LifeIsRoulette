package com.example.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roulettelife.R
import com.example.roulettelife.data.local.DiaryPreferences
import com.example.roulettelife.presentation.Screens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DiaryListScreen(
    navController: NavController,
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    val diaryPreferences = DiaryPreferences(context)
    var diaryEntries by remember { mutableStateOf(diaryPreferences.getAllDiaries()) }

    val diaryList = diaryEntries.entries.toList()

    val customFontFamily = FontFamily(
        Font(R.font.roboto_conde)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.diary),
                        style = TextStyle(
                            fontFamily = customFontFamily,
                            fontSize = 20.sp  // フォントサイズの調整も可能
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { onRouletteButtonClick() }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(diaryList.size) { index ->
                    val entry = diaryList[index]
                    val diaryEntryWithDate = entry.value.split("\n")
                    val diaryText = diaryEntryWithDate[0]
                    val diaryDate = if (diaryEntryWithDate.size > 1) diaryEntryWithDate[1] else stringResource(
                        id = R.string.not_available
                    )

                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        LaunchedEffect(dismissState) {
                            if (dismissState.dismissDirection == DismissDirection.EndToStart && dismissState.currentValue == DismissValue.DismissedToStart) {
                                diaryPreferences.removeDiary(entry.key)
                                diaryEntries = diaryPreferences.getAllDiaries()
                            } else {
                                dismissState.reset()  // 削除を回避
                            }
                        }
                    }

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
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        directions = setOf(DismissDirection.EndToStart),
                        dismissContent = {
                            // アイテム自体の背景を白に設定
                            DiaryListItem(
                                photoUri = entry.key,
                                diaryEntry = diaryText,
                                diaryDate = diaryDate,
                                onClick = {
                                    navController.navigate(Screens.DIARY_DETAIL.createRoute(entry.key))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)  // ここで背景を白に設定
                                    .padding(8.dp)
                            )
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
    diaryDate: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier  // Modifierを引数として受け取る
) {
    Row(
        modifier = modifier
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
