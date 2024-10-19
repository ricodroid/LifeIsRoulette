package com.ricodroid.roulettelife.presentation.home


import android.app.Activity
import android.text.TextPaint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ricodroid.roulettelife.R
import com.ricodroid.roulettelife.data.local.LocaleUtils.setLocale
import com.ricodroid.roulettelife.data.local.RoulettePreferences
import com.ricodroid.roulettelife.presentation.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteWeekdayScreen(
    navController: NavController,
    onSettingButtonClick: () -> Unit,
    onChangeRouletteButtonClick: () -> Unit,
    onDiaryButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // XML から defaultItems を取得
    val defaultItems = remember { context.resources.getStringArray(R.array.default_weed_day_roulette_items).toMutableList() }
    // 現在の言語を取得
    var currentLanguage by remember { mutableStateOf(roulettePreferences.getLanguage()) }

    // SharedPreferences から削除されたデフォルトアイテムとユーザーが追加した項目を取得
    val deletedDefaultItems = remember { roulettePreferences.getDeletedDefaultItems() }
    var allOptions = remember {
        mutableStateOf(
            // 削除されたデフォルトアイテムを除外して、ユーザーが追加したアイテムとデフォルトアイテムを結合
            roulettePreferences.getWeekdayRouletteItems() + defaultItems.filterNot { it in deletedDefaultItems }
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

    // ランダムな色のリストを作成
    val shuffledColors = remember {
        colors.shuffled().take(10)  // colors をシャッフルして10色を選択
    }

    // メニューの状態を保持
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // メニューのスライドコンポーネントを追加
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(screenWidth / 2)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),  // 上部をより透明に
                                Color.White.copy(alpha = 0.6f)   // 下部をやや濃く
                            )
                        )
                    )  // 半透明のすりガラス風背景
                    .padding(16.dp)
            ) {
                Text(
                    text = "Menu",
                    fontFamily = FontFamily(Font(R.font.menu_text, FontWeight.Bold)),
                    fontSize = 24.sp,
                    color = Color.Gray
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // 設定ボタン
                DrawerItem(text = "Settings", onClick = onSettingButtonClick)

                // 日記ボタン
                DrawerItem(text = "Diary", onClick = onDiaryButtonClick)

                // 言語変更オプション
                DrawerItem(text = "English", onClick = {
                    setLocale(context, "en")
                    roulettePreferences.saveLanguage("en")
                    currentLanguage = "en"
                    (context as? Activity)?.recreate()
                })

                DrawerItem(text = "日本語", onClick = {
                    setLocale(context, "ja")
                    roulettePreferences.saveLanguage("ja")
                    currentLanguage = "ja"
                    (context as? Activity)?.recreate()
                })
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        modifier = Modifier.shadow(8.dp), // 影を追加
                        title = {
                            // AnimatedVisibilityを使ってフェードイン・フェードアウトを実装
                            AnimatedVisibility(
                                visible = !drawerState.isOpen,  // メニューが開いていない時に表示
                                enter = fadeIn(animationSpec = tween(durationMillis = 2000)),  // フェードインを2秒に設定
                                exit = fadeOut(animationSpec = tween(durationMillis = 1500))   // フェードアウトも2秒に設定
                            )  {
                                Text(
                                    text = "Weekday Roulette",
                                    fontFamily = FontFamily(Font(R.font.menu_text, FontWeight.Normal)),
                                    color = Color(0xFF6699CC)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    drawerState.open()  // メニューを開く
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color(0xFFF1F3F4)
                        )
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF1F3F4))
                            .padding(padding)
                            .padding(13.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        RouletteSpinCalendarScreen()

                        Text(
                            text = selectedOption,
                            fontFamily = FontFamily(Font(R.font.open_sans, FontWeight.Normal)),
                            fontSize = 24.sp,
                            modifier = Modifier.padding(6.dp)
                        )

                        // ルーレット回転のコンテンツ
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(300.dp)
                                .background(Color.White.copy(alpha = 0.5f), shape = CircleShape)
                                .clickable(enabled = !isSpinning) {
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

                                            val selectedIndex =
                                                ((360f - finalRotation) / sliceAngle).toInt() % options.size

                                            selectedOption = options[selectedIndex]

                                            delay(3000)
                                            navController.navigate("${Screens.ACTION.route}/$selectedOption")
                                            roulettePreferences.saveRouletteSpinDate(LocalDate.now())
                                        }
                                    }
                                }
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

                                // 影を追加する
                                val shadowOffset = 10f // 影のオフセット
                                val shadowColor = Color(0xFFAAAAAA) // 影の色

                                // 影の円を描く（少し外側）
                                drawCircle(
                                    color = shadowColor,
                                    radius = radius,
                                    center = Offset(size.center.x + shadowOffset, size.center.y + shadowOffset),
                                    alpha = 0.5f // 影の透明度を調整
                                )

                                // ルーレットを描く
                                rotate(rotation) {
                                    for (i in options.indices) {
                                        drawArc(
                                            color = shuffledColors[i],
                                            startAngle = i * sliceAngle,
                                            sweepAngle = sliceAngle,
                                            useCenter = true
                                        )

                                        // 表示するテキストを制限（長すぎる場合はカットして"..."を追加）
                                        val displayText = if (options[i].length > 10) {
                                            options[i].take(4) + "..."
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

                                        // テキストを描画
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // ルーレットを平日用にするボタン
                        Card(
                            onClick = { onChangeRouletteButtonClick() }, // ここでCard自体をクリック可能に
                            modifier = Modifier
                                .width(200.dp)
                                .height(65.dp)
                                .padding(2.dp)
                                .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFFEF9) // 背景色を #FFFEF9 に変更
                            ),
                            elevation = CardDefaults.cardElevation(8.dp) // elevationの修正
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // アイコンを追加
                                Icon(
                                    imageVector = Icons.Default.Refresh, // 好きなアイコンに変更可能
                                    contentDescription = null,
                                    tint = Color(0xFF007DC5), // アイコンの色
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp) // アイコンとテキストの間にスペースを追加
                                )
                                // テキストを追加
                                Text(
                                    text = "Change Weekend",
                                    color = Color(0xFF6D6D6D), // テキストの色
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            onClick = { onDiaryButtonClick() }, // ここでCard自体をクリック可能に
                            modifier = Modifier
                                .width(200.dp)
                                .height(66.dp)
                                .padding(2.dp)
                                .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFFEF9) // 背景色を #FFFEF9 に変更
                            ),
                            elevation = CardDefaults.cardElevation(8.dp) // elevationの修正
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // アイコンを追加
                                Icon(
                                    imageVector = Icons.Default.DateRange, // 好きなアイコンに変更可能
                                    contentDescription = null,
                                    tint = Color(0xFFD93A49), // アイコンの色
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp) // アイコンとテキストの間にスペースを追加
                                )
                                // テキストを追加
                                Text(
                                    text = "Diary",
                                    color = Color(0xFF6D6D6D), // テキストの色
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }
    )
}