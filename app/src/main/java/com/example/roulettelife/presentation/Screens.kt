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

    object DIARY : Screens("diary/{photoUri}/{diaryEntry}") {
        fun createRoute(photoUri: String, diaryEntry: String): String {
            return "diary/${Uri.encode(photoUri)}/${Uri.encode(diaryEntry)}"
        }
    }
}
