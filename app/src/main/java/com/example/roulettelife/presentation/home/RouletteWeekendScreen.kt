package com.example.roulettelife.presentation.home

import android.app.Activity
import android.content.Context
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roulettelife.R
import com.example.roulettelife.data.local.LocaleUtils.setLocale
import com.example.roulettelife.data.local.RoulettePreferences
import com.example.roulettelife.presentation.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteWeekendScreen(
    navController: NavController,
    onSettingButtonClick: () -> Unit,
    onChangeRouletteButtonClick: () -> Unit,
    onDiaryButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // XML から defaultItems を取得
    val defaultItems = remember { context.resources.getStringArray(R.array.default_weekend_roulette_items).toMutableList() }
    // 現在の言語を取得
    var currentLanguage by remember { mutableStateOf(roulettePreferences.getLanguage()) }

    // SharedPreferences から削除されたデフォルトアイテムとユーザーが追加した項目を取得
    val deletedDefaultItems = remember { roulettePreferences.getDeletedDefaultItems() }
    var allOptions = remember {
        mutableStateOf(
            // 削除されたデフォルトアイテムを除外して、ユーザーが追加したアイテムとデフォルトアイテムを結合
            roulettePreferences.getWeekendRouletteItems() + defaultItems.filterNot { it in deletedDefaultItems }
        )
    }

    // ルーレットに表示する項目をランダムで10個選択
    val options by remember {
        mutableStateOf(
            if (allOptions.value.size > 10) {
                allOptions.value.shuffled().take(10)  // ランダムで10個選択
            } else {
                allOptions.value  // 10個未満ならそのまま表示
            }
        )
    }

    // ルーレットの回転角度を保持する状態
    var rotation by remember { mutableStateOf(0f) }
    var selectedOption by remember { mutableStateOf("") }
    var isSpinning by remember { mutableStateOf(false) }

    // CoroutineScopeを覚えておく
    val coroutineScope = rememberCoroutineScope()

    // ランダムな色のリストを作成
    val colors = remember {
        listOf(
            Color(0xFF00BCD4),  // サイアングリーン
            Color(0xFF9C27B0),  // パープル
            Color(0xFFFF5722),  // ビビッドオレンジ
            Color(0xFF3F51B5),  // インディゴブルー
            Color(0xFFFFEB3B),  // イエロー
            Color(0xFF4CAF50),  // グリーン
            Color(0xFF2196F3),  // ブルー
            Color(0xFFFFC107),  // アンバー
            Color(0xFF673AB7),  // ディープパープル
            Color(0xFFFFAB91),  // コーラル
            Color(0xFFC5CAE9),  // ライトインディゴ
            Color(0xFFFFF59D),  // ライトイエロー
            Color(0xFFB3E5FC),  // ライトブルー
            Color(0xFFFFE082),  // ライトゴールド
            Color(0xFFCE93D8),  // ライトパープル
            Color(0xFFFF9800),  // ディープオレンジ
            Color(0xFFCDDC39),  // ライム
            Color(0xFF009688),  // ティール
            Color(0xFF795548),  // ブラウン
            Color(0xFFE91E63),  // ピンク
            Color(0xFF607D8B),  // ブルーグレー
            Color(0xFF8BC34A),  // ライトグリーン
            Color(0xFF673AB7),  // バイオレット
            Color(0xFFD32F2F)   // ダークレッド
        )
    }


    // メニューの状態を保持
    var expanded by remember { mutableStateOf(false) }
    val poppinsFontFamily = FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weekend Roulette",
                        fontFamily = FontFamily(Font(R.font.poppins_regular, FontWeight.Normal)),
                        color = Color(0xFF6699CC)
                    )
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                expanded = false
                                onSettingButtonClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Diary") },
                            onClick = {
                                expanded = false
                                navController.navigate(Screens.DIARY_LIST.route)
                            }
                        )

                        // 言語変更オプション
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = {
                                setLocale(context, "en")
                                roulettePreferences.saveLanguage("en")
                                currentLanguage = "en"
                                (context as? Activity)?.recreate()  // アクティビティを再起動
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("日本語") },
                            onClick = {
                                setLocale(context, "ja")
                                roulettePreferences.saveLanguage("ja")
                                currentLanguage = "ja"
                                (context as? Activity)?.recreate()  // アクティビティを再起動
                                expanded = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFFFAB91)
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5DC))
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = selectedOption, modifier = Modifier.padding(18.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.LightGray, shape = CircleShape)
                ) {
                    // ルーレットの描画
                    Canvas(modifier = Modifier.size(250.dp)) {
                        val sliceAngle = 360f / options.size
                        val radius = size.minDimension / 2

                        val textPaint = TextPaint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        rotate(rotation) {
                            for (i in options.indices) {
                                drawArc(
                                    color = colors[i],
                                    startAngle = i * sliceAngle,
                                    sweepAngle = sliceAngle,
                                    useCenter = true
                                )

                                // 表示するテキストを制限（長すぎる場合はカットして"..."を追加）
                                val displayText = if (options[i].length > 10) {
                                    options[i].take(5) + "..."
                                } else {
                                    options[i]
                                }

                                val textAngle = i * sliceAngle + sliceAngle / 2
                                val textRadius = radius * 0.6f

                                val x = size.center.x + textRadius * kotlin.math.cos(
                                    Math.toRadians(textAngle.toDouble())
                                ).toFloat()
                                val y = size.center.y + textRadius * kotlin.math.sin(
                                    Math.toRadians(textAngle.toDouble())
                                ).toFloat()

                                drawContext.canvas.nativeCanvas.drawText(
                                    displayText,
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

                                val finalRotation = (rotation % 360f)
                                val sliceAngle = 360f / options.size

                                val selectedIndex = ((360f - finalRotation) / sliceAngle).toInt() % options.size

                                selectedOption = options[selectedIndex]

                                delay(3000)
                                navController.navigate("${Screens.ACTION.route}/$selectedOption")
                            }
                        }
                    },
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                    modifier = Modifier
                        .width(210.dp)
                        .height(100.dp)
                        .padding(16.dp)
                ) {
                    Text(text = if (isSpinning) "Spinning..." else "Spin")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ルーレットを平日用にするボタン
                Button(onClick = { onChangeRouletteButtonClick() }) {
                    Text(text = "Change Weekday")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onDiaryButtonClick() }) {
                    Text(text = "Diary")
                }
            }
        }
    )
}
