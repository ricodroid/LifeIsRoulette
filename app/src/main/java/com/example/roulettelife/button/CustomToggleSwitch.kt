package com.example.roulettelife.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.roulettelife.R
import kotlin.math.roundToInt

@Composable
fun CustomToggleSwitch(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    // スワイプ範囲を指定
    val sizePx = with(LocalDensity.current) { 200.dp.toPx() }
    var swipeableOffset by remember { mutableStateOf(if (isOn) sizePx else 0f) }  // スワイプ位置を保持

    // スワイプに基づいて色を補間
    val fraction = (swipeableOffset / sizePx).coerceIn(0f, 1f)  // 0～1の範囲に正規化
    val backgroundColor = androidx.compose.ui.graphics.lerp( Color(0xfffafdff), Color(0xffffff00), fraction)
    val toggleColor = Color.White

    // スワイプの進行に応じてトグル位置を調整
    val swipeableState = rememberDraggableState { delta ->
        swipeableOffset = (swipeableOffset + delta).coerceIn(0f, sizePx)  // オフセット位置の更新
    }

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(65.dp)
            .clip(CircleShape)  // pill shape
            .background(backgroundColor, shape = RoundedCornerShape(30.dp))  // 背景色とShapeを指定
            .shadow(4.dp, RoundedCornerShape(30.dp), ambientColor = Color.Gray.copy(alpha = 0.4f))  // 繊細な影を追加
            .draggable(
                state = swipeableState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    // スワイプ終了時にトグルの状態を決定
                    val isOnNew = swipeableOffset > sizePx / 2
                    onToggle(isOnNew)  // 状態を更新
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // トグル部分のデザイン
        Box(
            modifier = Modifier
                .offset { IntOffset(swipeableOffset.roundToInt(), 0) }  // スワイプ位置を調整
                .size(65.dp)  // トグルのサイズ
                .clip(CircleShape)
                .background(toggleColor),
            contentAlignment = Alignment.Center
        ) {
            // アイコンを切り替え（CheckCircleはImageVector、elseはリソースから読み込む）
            if (fraction > 0.5f) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color.Gray,  // アイコンの色
                    modifier = Modifier.size(24.dp)  // アイコンのサイズ
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_camera_alt_24),  // リソースからアイコンを読み込む
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)  // アイコンのサイズ
                )
            }
        }
    }
}
