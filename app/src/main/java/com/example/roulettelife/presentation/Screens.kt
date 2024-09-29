package com.example.roulettelife.presentation

sealed class Screens(val route: String) {
    object ROULETTE_WEEKDAY : Screens("roulette_weekday")
    object ROULETTE_WEEKEND : Screens("roulette_weekend")
    object ROULETTE_WEEKEND_SETTINGS : Screens("roulette_weekend_settings")
    object ROULETTE_WEEKDAY_SETTINGS : Screens("roulette_weekday_settings")

    // パラメータ付きのルートを定義
    object ACTION : Screens("action/{selectedItem}") {
        fun createRoute(selectedItem: String) = "action/$selectedItem"
    }

    object MAP : Screens("map")
}
