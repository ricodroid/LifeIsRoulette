package com.example.roulettelife.presentation.action
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roulettelife.data.local.DiaryPreferences
import com.example.roulettelife.presentation.Screens
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActionScreen(
    selectedItem: String,
    navController: NavController,
    onPhotoSaved: (Uri, String) -> Unit
) {
    val currentDate = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date())
    val context = LocalContext.current  // LocalContextからコンテキストを取得
    val diaryPreferences = DiaryPreferences(context)

    // LaunchedEffectで画面表示時の処理を実行
    LaunchedEffect(selectedItem) {
        // selectedItemが空でない場合はそのまま保存、空の場合はSharedPreferencesから取得
        val itemToSave = selectedItem.ifBlank {
            diaryPreferences.getSelectedItem() ?: ""
        }

        if (itemToSave.isNotBlank()) {
            diaryPreferences.saveSelectedItem(itemToSave)
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // ローカル関数として写真を保存する処理
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


        // 写真の保存処理を実行
        bitmap?.let {
            val uri = savePhotoToExternalStorage(context, bitmap)
            val diaryEntry = "今日は $selectedItem を完了しました。" // 日記内容のサンプル
            onPhotoSaved(uri, diaryEntry)

            // SharedPreferencesから選択された項目を削除
            diaryPreferences.removeSelectedItem()

            // DiaryScreen に遷移
            navController.navigate(Screens.DIARY.createRoute(uri.toString(), diaryEntry))
        }
    }

    // UI構成
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 可愛い日付表示
        Text(
            text = "今日は $currentDate です",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEE82EE),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // メッセージ表示
        Text(
            text = "さぁ！退屈な一日を、思い出深い一日に変えよう！",
            fontSize = 18.sp,
            color = Color(0xFF8A2BE2),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 選択された項目の表示
        Text(
            text = "選択された項目: $selectedItem",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Doneボタン
        Button(
            onClick = { photoLauncher.launch(null) },  // カメラを起動する
            shape = CircleShape,
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFF00BFFF))
        ) {
            Text(
                text = "Done",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


// この項目を削除するかどうかをPOPUPする。
// Action完了ボタンをタップすると、現在の緯度経度を記憶し、地図画面に遷移する。
// https://qiita.com/marchin_1989/items/9f8e2852fa2cbf25d5ae → Maps SDK for Android
// 完了ボタンをタップした場所にアイコンが立ちActionの項目とともにピン付けされる。
// memoボタンをタップすると、小さい日記と、一枚の写メを保存できるようになる。