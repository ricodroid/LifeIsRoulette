package com.example.roulettelife.presentation.action

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ActionScreen(
    selectedItem: String,
    onMapButtonClick: () -> Unit
) {
    // selectedItem を使って何かしらの処理を行う
    Text(text = "選択された項目: $selectedItem")

    // この項目を削除するかどうかをPOPUPする。
    // Action完了ボタンをタップすると、現在の緯度経度を記憶し、地図画面に遷移する。
    // https://qiita.com/marchin_1989/items/9f8e2852fa2cbf25d5ae → Maps SDK for Android
    // 完了ボタンをタップした場所にアイコンが立ちActionの項目とともにピン付けされる。
    // memoボタンをタップすると、小さい日記と、一枚の写メを保存できるようになる。
    Button(onClick = onMapButtonClick) {
        Text(text = "地図を表示する")
    }
}
