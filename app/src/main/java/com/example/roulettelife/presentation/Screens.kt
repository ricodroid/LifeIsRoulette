package com.example.roulettelife.presentation

import android.net.Uri

sealed class Screens(val route: String) {
    object ROULETTE_WEEKDAY : Screens("roulette_weekday")
    object ROULETTE_WEEKEND : Screens("roulette_weekend")
    object ROULETTE_WEEKEND_SETTINGS : Screens("roulette_weekend_settings")
    object ROULETTE_WEEKDAY_SETTINGS : Screens("roulette_weekday_settings")

    object ACTION : Screens("action/{selectedItem}") {
        fun createRoute(selectedItem: String) = "action/$selectedItem"
    }


    // 日記一覧画面のルート
    object DIARY_LIST : Screens("diary_list")

    // 日記詳細画面のルート（photoUriに基づく）
    object DIARY_DETAIL : Screens("diary_detail/{photoUri}") {
        fun createRoute(photoUri: String): String {
            return "diary_detail/${Uri.encode(photoUri)}"
        }
    }

    // 日記編集画面のルート（写真のURIと日記内容）
    object DIARY : Screens("diary/{photoUri}/{diaryEntry}") {
        fun createRoute(photoUri: String, diaryEntry: String): String {
            return "diary/${Uri.encode(photoUri)}/${Uri.encode(diaryEntry)}"
        }
    }
}
