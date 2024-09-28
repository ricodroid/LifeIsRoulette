package com.example.roulettelife.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RouletteScreen() {
    // ルーレットの選択肢
    val options = listOf("踊る", "無駄な買い物をする", "猫の写真を取る", "イベントに参加する", "勉強する")

    // ルーレットの回転角度を保持する状態
    var rotation by remember { mutableStateOf(0f) }
    var selectedOption by remember { mutableStateOf("") }
    var isSpinning by remember { mutableStateOf(false) }

    // CoroutineScopeを覚えておく
    val coroutineScope = rememberCoroutineScope()

    // ランダムな色のリストを作成
    val colors = remember {
        List(options.size) {
            Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat()
            )
        }
    }

    // UI構成
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Selected Option: $selectedOption", modifier = Modifier.padding(16.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            Canvas(modifier = Modifier.size(250.dp)) {
                // ルーレットの回転を描画
                val sliceAngle = 360f / options.size
                rotate(rotation) {
                    for (i in options.indices) {
                        drawArc(
                            color = colors[i],  // ランダムに生成された色を使う
                            startAngle = i * sliceAngle,
                            sweepAngle = sliceAngle,
                            useCenter = true
                        )
                    }
                }
            }

            // ポインターを描画するための別のCanvas
            Canvas(modifier = Modifier
                .size(320.dp)  // ルーレットより少し大きいサイズを指定
                .offset(y = (-4).dp)  // ポインターをルーレットの上外側に配置
            ) {
                val pointerPath = Path().apply {
                    moveTo(size.width / 2 - 30, 0f)  // 左側の点
                    lineTo(size.width / 2 + 30, 0f)  // 右側の点
                    lineTo(size.width / 2, 90f)  // 下側の点（ポインターを大きくする）
                    close()  // 三角形を閉じる
                }
                drawPath(pointerPath, Color.Red)  // ポインターの色を赤に設定
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    selectedOption = ""
                    // CoroutineScopeを使用して回転を開始
                    coroutineScope.launch {
                        for (i in 1..20) {
                            rotation += Random.nextFloat() * 360
                            delay(100)
                        }
                        isSpinning = false
                        val selectedIndex = (rotation / (360f / options.size)).toInt() % options.size
                        selectedOption = options[selectedIndex]
                    }
                }
            },
            enabled = !isSpinning
        ) {
            Text(text = if (isSpinning) "Spinning..." else "Spin the Roulette")
        }
    }
}

@Preview
@Composable
fun PreviewRouletteScreen() {
    RouletteScreen()
}
