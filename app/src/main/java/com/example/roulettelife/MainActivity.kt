package com.example.roulettelife

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.roulettelife.data.local.LocaleUtils
import com.example.roulettelife.data.local.RoulettePreferences
import com.example.roulettelife.presentation.AppScaffold
import com.example.roulettelife.presentation.permission.PermissionsHelper
import com.example.roulettelife.presentation.permission.PermissionsHelper.isBluetoothEnabled
import com.example.roulettelife.presentation.permission.PermissionsHelper.isGPSEnabled
import com.example.roulettelife.ui.theme.RouletteLifeTheme


class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ステータスバーの色を設定
        window.statusBarColor = Color.parseColor("#F1F3F4")

        // アプリ起動時に権限の許可状態をログに表示
        PermissionsHelper.logPermissionStatus(this)

        // 言語設定を適用
        val roulettePreferences = RoulettePreferences(this)
        val language = roulettePreferences.getLanguage() ?: "en"  // デフォルトは英語
        LocaleUtils.setLocale(this, language)

        // パーミッションリクエストの初期化
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // パーミッションの結果をハンドルする
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                // 全てのパーミッションが許可された場合
                Log.d("Permissions", "All permissions granted")
            } else {
                // 一部のパーミッションが拒否された場合
                Log.d("Permissions", "Some permissions denied")
            }
        }

//        requestSystemPermissions()

        // アプリの開始時に全てのパーミッションをリクエスト
        if (!PermissionsHelper.allPermissionsGranted(this)) {
            requestAllPermissions()
        }

        setContent {
            RouletteLifeTheme {
                // 背景色をテーマから取得して Surface を使う
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(this)
                }
            }
        }
    }

    private fun requestAllPermissions() {
        val requiredPermissions = PermissionsHelper.getAllRequiredPermissions().toTypedArray()
        permissionLauncher.launch(requiredPermissions)
    }

    private fun requestSystemPermissions() {
        // BluetoothがOFFの場合にONを促す
        if (!isBluetoothEnabled()) PermissionsHelper.enableBluetooth(this)

        // GPSがOFFの場合にONを促す
        if (!isGPSEnabled(this))  PermissionsHelper.enableGPS(this)
    }
}