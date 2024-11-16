package com.ricodroid.roulettelife.presentation.action

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.ricodroid.roulettelife.animation.DancingDotsIndicator
import com.ricodroid.roulettelife.button.CustomToggleSwitch
import com.ricodroid.roulettelife.data.local.DiaryPreferences
import com.ricodroid.roulettelife.presentation.Screens
import java.text.SimpleDateFormat
import java.util.*
import com.ricodroid.roulettelife.R
import java.io.File

// ハイライトを一番上にする。
// Doneは二番目
// その次に日付
// 今日のハイライトは削除する
// Listもインスタのように写真メインにする

@Composable
fun ActionScreen(
    selectedItem: String,
    navController: NavController,
    onPhotoSaved: (Uri, String) -> Unit
) {
    val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())  // 日付フォーマットを英語に変更
    val context = LocalContext.current
    val diaryPreferences = DiaryPreferences(context)
    var isOn by remember { mutableStateOf(false) }
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    // LaunchedEffectで画面表示時の処理を実行
    LaunchedEffect(selectedItem) {
        val itemToSave = selectedItem.ifBlank {
            diaryPreferences.getSelectedItem() ?: ""
        }

        if (itemToSave.isNotBlank()) {
            diaryPreferences.saveSelectedItem(itemToSave)
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri.value != null) {
            val uri = photoUri.value!!
            val diaryEntry = context.getString(R.string.completed_item, selectedItem)
            onPhotoSaved(uri, diaryEntry)

            // SharedPreferencesから選択された項目を削除
            diaryPreferences.removeSelectedItem()

            // DiaryScreen に遷移
            navController.navigate(Screens.DIARY.createRoute(uri.toString(), diaryEntry))
        }
    }

    // カスタムフォントを設定（Poppinsなどを使用）
    val poppinsFontFamily = FontFamily(
        Font(R.font.open_sans, FontWeight.Normal)
    )

    // UI構成
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // テキストと下線の間隔を調整
            contentAlignment = Alignment.BottomCenter
        ) {
            // テキスト
            Text(
                text = "$selectedItem !",
                fontSize = 30.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(Font(R.font.open_sans, FontWeight.Normal)),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3155A6),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,  // 行間の調整
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillMaxWidth()
            )

            // おしゃれな下線
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)  // 下線の幅を調整
                    .height(2.dp)  // 下線の高さ（太さ）を調整
                    .background(Color(0xFFED1A3D).copy(alpha = 0.7f))  // 赤系の強調色に透明度を持たせた線
                    .align(Alignment.BottomCenter)  // 下線を中央揃えに
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // 日付表示（モダンでシンプルなフォントと色）
        Text(
            text = context.getString(R.string.today_item, currentDate),
            fontSize = 22.sp,  // 小さめのフォントサイズ
            fontWeight = FontWeight.Normal,
            fontFamily = poppinsFontFamily,
            color = Color(0xFF555555),  // 落ち着いたグレー
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

        Spacer(modifier = Modifier.height(12.dp))

        // メッセージ表示（シンプルで優しいフォントと色）
        Text(
            text = stringResource(id = R.string.lets_turn),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = poppinsFontFamily,
            color = Color(0xFF666666),  // 落ち着いたトーン
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

        Spacer(modifier = Modifier.height(12.dp))

        // カスタムトグルスイッチ
        CustomToggleSwitch(
            isOn = isOn,
            onToggle = { newState ->
                isOn = newState  // スイッチの状態を更新
                if (isOn) {
                    val uri = createImageFileUri(context) // 保存先のURIを作成
                    photoUri.value = uri
                    photoLauncher.launch(uri) // カメラを起動
                }
            }
        )

        Spacer(modifier = Modifier.height(60.dp))

        // 踊るインジケーターを追加（柔らかい色でシンプルに）
        DancingDotsIndicator()
    }
}

fun createImageFileUri(context: Context): Uri {
    val timestamp = System.currentTimeMillis()
    val fileName = "photo_$timestamp.jpg"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(storageDir, fileName)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

