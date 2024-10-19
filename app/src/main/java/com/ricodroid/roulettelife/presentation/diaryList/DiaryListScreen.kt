package com.ricodroid.roulettelife.presentation.diaryList

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.ricodroid.roulettelife.R
import com.ricodroid.roulettelife.data.local.DiaryPreferences
import com.ricodroid.roulettelife.presentation.Screens

@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun DiaryListScreen(
    navController: NavController,
    context: Context,
    onRouletteButtonClick: () -> Unit,
) {
    val diaryPreferences = DiaryPreferences(context)
    var diaryEntries by remember { mutableStateOf(diaryPreferences.getAllDiaries()) }

    val diaryList = diaryEntries.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.diary),
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.menu_text)),
                            fontSize = 20.sp
                        ),
                        color = Color(0xFF6699CC)
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3列に設定して正方形グリッドに
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(4.dp) // 写真周りの余白
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
                                dismissState.reset()
                            }
                        }
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF333333))
                                    .padding(1.dp),
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
                            // DiaryListItemを正方形の枠に表示する
                            DiaryGridItem(
                                photoUri = entry.key,
                                diaryEntry = diaryText,
                                diaryDate = diaryDate,
                                onClick = {
                                    navController.navigate(Screens.DIARY_DETAIL.createRoute(entry.key))
                                },
                                modifier = Modifier
                                    .aspectRatio(1f)  // 正方形にするためのアスペクト比設定
                                    .padding(1.dp)
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun DiaryGridItem(
    photoUri: String,
    diaryEntry: String,
    diaryDate: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(Color.White)
    ) {
        // 写真のURIが有効なら表示、正方形でトリミング
        if (photoUri.isNotEmpty()) {
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = stringResource(id = R.string.diary_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // 正方形にトリミング
                contentScale = ContentScale.Crop
            )
        }

        // 日記の一部を表示（オプションとして）
        Text(
            text = diaryEntry,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(4.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
