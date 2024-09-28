package com.example.roulettelife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.roulettelife.presentation.home.RouletteScreen
import com.example.roulettelife.ui.theme.RouletteLifeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RouletteLifeTheme {
                // 背景色をテーマから取得して Surface を使う
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ここで RouletteScreen を呼び出してルーレット画面を表示
                    RouletteScreen()
                }
            }
        }
    }
}