package com.example.roulettelife.button

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    val sizePx = with(LocalDensity.current) { 250.dp.toPx() } // スイッチの幅をpxに変換
    val anchors = mapOf(0f to 0, sizePx to 1)  // 0からスイッチ幅までの範囲でスワイプ

    // 色を線形補間 (lerp) で計算
    val fraction = (swipeableState.offset.value / sizePx).coerceIn(0f, 1f)  // 0～1の範囲に正規化

    val backgroundColor = androidx.compose.ui.graphics.lerp(Color(0xffeee9e6), Color(0xffeddc44), fraction)
    // トグルの色設定
    val toggleColor = Color.White

    // アイコンを切り替え
    val icon: ImageVector = if (swipeableState.currentValue == 1) Icons.Filled.CheckCircle else Icons.Filled.Refresh

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
            .width(250.dp)
            .height(65.dp)
            .clip(RoundedCornerShape(30.dp))  // pill shape
            .background(backgroundColor, shape = RoundedCornerShape(30.dp))  // 背景色とShapeを指定
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
                .size(65.dp)  // トグルのサイズを大きく
                .clip(CircleShape)
                .background(toggleColor),
            contentAlignment = Alignment.Center
        ) {
            // アイコンを追加
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,  // アイコンの色
                modifier = Modifier.size(24.dp)  // アイコンのサイズ
            )
        }
    }
}
