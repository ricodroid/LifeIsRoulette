package com.example.roulettelife.button

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomToggleSwitch(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    // スワイプ範囲を指定
    val swipeableState = rememberSwipeableState(initialValue = if (isOn) 1 else 0)

    // スワイプ時の範囲を計算
    val sizePx = with(LocalDensity.current) { 120.dp.toPx() } // スイッチの幅をpxに変換
    val anchors = mapOf(0f to 0, sizePx to 1)  // 0からスイッチ幅までの範囲でスワイプ

    // 背景色の設定
    val backgroundColor = if (swipeableState.currentValue == 1) Color(0xFF4CAF50) else Color(0xFFD6D6D6)
    val toggleColor = Color.White

    // スワイプ完了時に状態を監視してonToggleを呼び出す
    LaunchedEffect(swipeableState.targetValue) {
        if (swipeableState.targetValue == 1) {
            onToggle(true)  // ONの状態にする
        } else if (swipeableState.targetValue == 0) {
            onToggle(false)  // OFFの状態にする
        }
    }

    // スイッチ全体のデザイン
    Box(
        modifier = Modifier
            .width(120.dp)  // 幅を拡大
            .height(60.dp)  // 高さを拡大
            .clip(RoundedCornerShape(30.dp))  // pill shape
            .background(backgroundColor)  // 背景色
            .swipeable(  // スワイプジェスチャーを検知
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },  // 50%を超えたらトグル
                orientation = Orientation.Horizontal
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // トグル部分のデザイン
        Box(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }  // スワイプ位置を調整
                .size(60.dp)  // トグルのサイズを大きく
                .clip(CircleShape)
                .background(toggleColor)
        )
    }
}