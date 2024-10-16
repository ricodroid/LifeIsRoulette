package com.example.roulettelife.presentation.action

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavController
import com.example.roulettelife.R
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

@RequiresApi(Build.VERSION_CODES.Q)
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
        Font(R.font.poppins_regular, FontWeight.Normal)
    )

    // UI構成
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))  // モダンなダーク背景
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = selectedItem,
            fontSize = 26.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFED1A3D),
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

        // 日付表示（モダンなフォントと色）
        Text(
            text = context.getString(R.string.today_item, currentDate),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = poppinsFontFamily,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

        // メッセージ表示（英語に翻訳）
        Text(
            text = stringResource(id = R.string.lets_turn),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center  // テキストを中央寄せに設定
        )

        // 選択された項目の表示
//        Text(
//            text = stringResource(id = R.string.highlight),
//            fontSize = 22.sp,
//            fontStyle = FontStyle.Italic,
//            fontFamily = poppinsFontFamily,
//            color = Color(0xFF333333),
//            modifier = Modifier.padding(bottom = 12.dp),
//            textAlign = TextAlign.Center  // テキストを中央寄せに設定
//        )

        CustomToggleSwitch(
            isOn = isOn,
            onToggle = { newState ->
                isOn = newState  // スイッチの状態を更新
                if (isOn) {
                    photoLauncher.launch(null)  // スイッチがONになった時にカメラを起動
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
