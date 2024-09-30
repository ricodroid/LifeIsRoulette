package com.example.roulettelife

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.roulettelife.presentation.AppScaffold
import com.example.roulettelife.presentation.permission.PermissionsHelper
import com.example.roulettelife.presentation.permission.PermissionsHelper.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
import com.example.roulettelife.presentation.permission.PermissionsHelper.BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE
import com.example.roulettelife.ui.theme.RouletteLifeTheme


class MainActivity : ComponentActivity() {


    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // パーミッションの結果を受け取る
            val allGranted = permissions.all { it.value }  // 全てのパーミッションが許可されたかを確認
            if (allGranted) {
                Log.d("MainActivity", "全てのパーミッションが許可されました")
            } else {
                Log.d("MainActivity", "一部のパーミッションが拒否されました")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionsHelper.hasAllPermissions(this)) {
            PermissionsHelper.requestAllPermissionsWithLauncher(this, requestPermissionsLauncher)
        }

        setContent {
            RouletteLifeTheme {
                // 背景色をテーマから取得して Surface を使う
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold()
                }
            }
        }
    }

}