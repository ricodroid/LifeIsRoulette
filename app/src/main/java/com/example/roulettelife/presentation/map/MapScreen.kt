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
fun MapScreen(currentPosition: LatLng) {
    val defaultZoom = 15f  // ズームレベルを変更

    // カメラの初期位置とズームレベルを設定
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, defaultZoom)
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
        uiSettings = uiSettings
    ) {
        // 現在地にマーカーを追加
        Marker(
            state = rememberMarkerState(position = currentPosition),
            title = "現在地",
            snippet = "ここがあなたの位置です"
        )
    }
}