package com.example.roulettelife.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.nio.file.WatchEvent

@Composable
fun MapScreen() {
    val defaultPosition = LatLng(35.689501, 139.691722) // 東京都庁の位置
    val defaultZoom = 8f

    // カメラの初期位置とズームレベルを設定
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, defaultZoom)
    }

    // UI 設定 (ズームやスクロールの有効/無効など)
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,  // ズームボタンを有効にする
            myLocationButtonEnabled = true // 現在位置ボタンを有効にする
        )
    }

    // マップの表示
    GoogleMap(
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,

    ) {
        // マーカーの追加 (例: 東京都庁)
        Marker(
            state = rememberMarkerState(position = defaultPosition),
            title = "東京都庁",
            snippet = "東京都のランドマーク"
        )
    }
}
