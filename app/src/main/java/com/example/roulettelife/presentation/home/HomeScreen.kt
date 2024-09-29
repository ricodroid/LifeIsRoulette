package com.example.roulettelife.presentation.home

import android.text.TextPaint
import android.util.Log
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
    val options by remember { mutableStateOf(roulettePreferences.getWeekendRouletteItems()) }

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
        Text(text = "Let's: $selectedOption", modifier = Modifier.padding(18.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            // ルーレットの描画
            Canvas(modifier = Modifier.size(250.dp)) {
                // 各セクションの角度を計算
                val sliceAngle = 360f / options.size
                val radius = size.minDimension / 2

                // 文字を描くためのTextPaintを作成
                val textPaint = TextPaint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
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
                        val textRadius = radius * 0.6f

                        // テキストの描画位置を計算
                        val x = size.center.x + textRadius * kotlin.math.cos(
                            Math.toRadians(textAngle.toDouble())
                        ).toFloat()
                        val y = size.center.y + textRadius * kotlin.math.sin(
                            Math.toRadians(textAngle.toDouble())
                        ).toFloat()

                        // テキストを描画
                        drawContext.canvas.nativeCanvas.drawText(
                            options[i],
                            x,
                            y,
                            textPaint
                        )
                    }
                }
            }

            // ポインターを描画
            Canvas(modifier = Modifier
                .size(320.dp)
                .offset(x = 0.dp, y = 0.dp) // オフセットをリセット
            ) {
                val pointerPath = Path().apply {
                    // ポインターをルーレットの右中央（3時の方向）に配置
                    moveTo(size.width, size.height / 2 - 30)  // 右中央の頂点
                    lineTo(size.width, size.height / 2 + 30)  // 右中央の反対側頂点
                    lineTo(size.width - 90, size.height / 2)  // ポインターの先端（中央寄り）
                    close()  // 三角形を閉じる
                }
                drawPath(pointerPath, Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ボタンをクリックしてルーレットを回転
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    selectedOption = ""
                    coroutineScope.launch {
                        for (i in 1..20) {
                            rotation += Random.nextFloat() * 360
                            delay(100)
                        }
                        isSpinning = false

                        // 最終的な回転角度を取得
                        val finalRotation = (rotation % 360f)
                        val sliceAngle = 360f / options.size

                        // ポインターが指しているセクションのインデックスを計算
                        val selectedIndex = ((360f - finalRotation) / sliceAngle).toInt() % options.size
                        
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

