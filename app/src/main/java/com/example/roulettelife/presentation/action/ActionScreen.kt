package com.example.roulettelife.presentation.action
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActionScreen(
    selectedItem: String,
    onMapButtonClick: () -> Unit
) {
    val currentDate = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date())

    // UI構成
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 可愛い日付表示
        Text(
            text = "今日は $currentDate です",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEE82EE),  // 可愛いピンク系の色
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // さぁ！退屈な一日を、思い出深い一日に変えよう！というメッセージ
        Text(
            text = "さぁ！退屈な一日を、思い出深い一日に変えよう！",
            fontSize = 18.sp,
            color = Color(0xFF8A2BE2),  // バイオレット系の可愛い色
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 選択された項目の表示
        Text(
            text = "選択された項目: $selectedItem",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 「$selectedItem を完了したら、その場所でDoneボタンをタップしよう」
        Text(
            text = "$selectedItem を完了したら、その場所でDoneボタンをタップしよう",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Doneボタンを球体で表示する
        Button(
            onClick = onMapButtonClick,
            shape = CircleShape,  // 球体にする
            modifier = Modifier
                .size(100.dp)  // ボタンのサイズを設定
                .background(Color(0xFF00BFFF))  // ボタンの色をライトブルーに
        ) {
            Text(
                text = "Done",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 地図を表示するボタン
//        Button(onClick = onMapButtonClick) {
//            Text(text = "地図を表示する")
//        }
    }
}

// この項目を削除するかどうかをPOPUPする。
// Action完了ボタンをタップすると、現在の緯度経度を記憶し、地図画面に遷移する。
// https://qiita.com/marchin_1989/items/9f8e2852fa2cbf25d5ae → Maps SDK for Android
// 完了ボタンをタップした場所にアイコンが立ちActionの項目とともにピン付けされる。
// memoボタンをタップすると、小さい日記と、一枚の写メを保存できるようになる。