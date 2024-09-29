package com.example.roulettelife.presentation.action

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ActionScreen(
    selectedItem: String
) {
    // selectedItem を使って何かしらの処理を行う
    Text(text = "選択された項目: $selectedItem")

    // この項目を削除するかどうかをPOPUPする。
    // Action完了ボタンをタップすると、現在の緯度経度を記憶し、地図画面に遷移する。
    // 完了ボタンをタップした場所にアイコンが立ちActionの項目とともにピン付けされる。
    // memoボタンをタップすると、小さい日記と、一枚の写メを保存できるようになる。
}
