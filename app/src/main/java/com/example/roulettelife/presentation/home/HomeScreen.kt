package com.example.roulettelife.presentation.home

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roulettelife.data.local.RoulettePreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen(
    onSettingButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // ルーレットの選択肢
    val options by remember { mutableStateOf(roulettePreferences.getRouletteItems() ) }

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
                val radius = size.minDimension / 2

                // 文字を描くためのTextPaintを作成
                val textPaint = TextPaint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                rotate(rotation) {
                    for (i in options.indices) {
                        // セクションの色を描画
                        drawArc(
                            color = colors[i],
                            startAngle = i * sliceAngle,
                            sweepAngle = sliceAngle,
                            useCenter = true
                        )

                        // 各セクションの中心角度を計算
                        val textAngle = i * sliceAngle + sliceAngle / 2
                        val textRadius = radius * 0.6f  // テキストの配置位置

                        // テキストの位置を計算
                        val x = size.center.x + textRadius * kotlin.math.cos(Math.toRadians(textAngle.toDouble())).toFloat()
                        val y = size.center.y + textRadius * kotlin.math.sin(Math.toRadians(textAngle.toDouble())).toFloat()

                        // テキストの最大幅を設定 (セクション内に収めるため)
                        val maxTextWidth = textRadius * 2  // テキストの最大幅

                        // テキストを折り返しながら描画する
                        val wrappedText = StringBuilder()
                        var start = 0
                        while (start < options[i].length) {
                            val count = textPaint.breakText(options[i], start, options[i].length, true, maxTextWidth, null)
                            wrappedText.append(options[i], start, start + count)
                            wrappedText.append("\n")
                            start += count
                        }

                        // テキストを描画
                        drawContext.canvas.nativeCanvas.drawText(
                            wrappedText.toString(),
                            x,
                            y,
                            textPaint
                        )


                    }
                }
            }

            // ポインターを描画するための別のCanvas
            Canvas(modifier = Modifier
                .size(320.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        // 設定画面に移動するボタン
        Button(onClick = { onSettingButtonClick() }) {
            Text(text = "Go to Settings")
        }
    }
}

//@Preview
//@Composable
//fun PreviewRouletteScreen() {
//    HomeScreen(
//
//    )
//}
