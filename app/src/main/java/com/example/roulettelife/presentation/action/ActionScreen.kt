package com.example.roulettelife.presentation.action

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavController
import com.example.roulettelife.R
import com.example.roulettelife.animation.DancingDotsIndicator
import com.example.roulettelife.button.CustomToggleSwitch
import com.example.roulettelife.data.local.DiaryPreferences
import com.example.roulettelife.presentation.Screens
import java.text.SimpleDateFormat
import java.util.*

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

    // LaunchedEffectで画面表示時の処理を実行
    LaunchedEffect(selectedItem) {
        val itemToSave = selectedItem.ifBlank {
            diaryPreferences.getSelectedItem() ?: ""
        }

        if (itemToSave.isNotBlank()) {
            diaryPreferences.saveSelectedItem(itemToSave)
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        fun savePhotoToExternalStorage(context: Context, bitmap: Bitmap): Uri {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.let { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                }
            }

            return uri ?: Uri.EMPTY
        }

        bitmap?.let {
            val uri = savePhotoToExternalStorage(context, bitmap)
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
            .background(Color(0xFFF5F5F5))  // 柔らかくて繊細な背景色
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
                text = selectedItem,
                fontSize = 30.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(Font(R.font.poppins_regular, FontWeight.Normal)),
                fontWeight = FontWeight.SemiBold,  // 少し控えめな太さ
                color = Color(0xFF444444),  // 柔らかいグレー
                textAlign = TextAlign.Center,  // テキストを中央寄せに設定
                modifier = Modifier.padding(bottom = 15.dp)  // 下線とテキストのスペース
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

        // 日付表示（モダンでシンプルなフォントと色）
        Text(
            text = context.getString(R.string.today_item, currentDate),
            fontSize = 20.sp,  // 小さめのフォントサイズ
            fontWeight = FontWeight.Normal,
            fontFamily = poppinsFontFamily,
            color = Color(0xFF555555),  // 落ち着いたグレー
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

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

        // カスタムトグルスイッチ
        CustomToggleSwitch(
            isOn = isOn,
            onToggle = { newState ->
                isOn = newState  // スイッチの状態を更新
                if (isOn) {
                    photoLauncher.launch(null)  // スイッチがONになった時にカメラを起動
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 踊るインジケーターを追加（柔らかい色でシンプルに）
        DancingDotsIndicator()
    }
}
