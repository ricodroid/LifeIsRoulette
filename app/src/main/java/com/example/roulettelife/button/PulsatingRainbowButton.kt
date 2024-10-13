package com.example.roulettelife.button

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PulsatingRainbowButton(onClick: () -> Unit) {
    // アニメーションのための状態
    var isPulsing by remember { mutableStateOf(false) }

    // アニメーションによるサイズ変化
    val sizeAnimation by animateFloatAsState(
        targetValue = if (isPulsing) 1.2f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),  // アニメーションの速さを指定
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    //  グラデーション
    val rainbowBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFA8A8),  // ピンク
            Color(0xFFFF70A8),  // ピンク系（明るめ）
            Color(0xFFFF1DBB),  // マゼンタピンク
            Color(0xFFFD1D1D),  // 赤
            Color(0xFFF56040),  // オレンジ
            Color(0xFFFFA34D),  // 明るいオレンジ
            Color(0xFFFFC300)   // ゴールド系
        )
    )

    // 虹色のボタン（脈打つエフェクトを追加）
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(Color.Transparent),  // 虹色を背景にするための設定
        modifier = Modifier
            .size(180.dp)  // ボタンのサイズを大きく
            .background(brush = rainbowBrush, shape = CircleShape)
    ) {
        Box(
            contentAlignment = Alignment.Center,  // ボタン内のテキストを中央に配置
            modifier = Modifier.fillMaxSize()  // ボックス全体に広げる
        ) {
            Text(
                text = "Done",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}
