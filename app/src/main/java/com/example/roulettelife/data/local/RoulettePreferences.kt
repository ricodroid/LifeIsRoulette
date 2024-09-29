package com.example.roulettelife.data.local

// SharedPreferencesでルーレットの目を管理
import android.content.Context
import android.content.SharedPreferences

class RoulettePreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val ROULETTE_PREF_KEY_WEEKEND = "roulette_items_weekend"
        private const val ROULETTE_PREF_KEY_WEEKDAY = "roulette_items_weekday"
        private const val PREFERENCES_NAME = "roulette_preferences"
    }


    // 休日のルーレット項目を取得
    fun getWeekendRouletteItems(): List<String> {
        val itemsString = sharedPreferences.getString(ROULETTE_PREF_KEY_WEEKEND, "") ?: ""
        return if (itemsString.isNotBlank()) {
            itemsString.split(",")
        } else {
            emptyList()
        }
    }

    // 平日のルーレット項目を取得
    fun getWeekdayRouletteItems(): List<String> {
        val itemsString = sharedPreferences.getString(ROULETTE_PREF_KEY_WEEKDAY, "") ?: ""
        return if (itemsString.isNotBlank()) {
            itemsString.split(",")
        } else {
            emptyList()
        }
    }

    // 休日のルーレット項目を追加して保存（Stringを引数に取る）
    fun saveWeekendRouletteItems(newItem: String) {
        val items = getWeekendRouletteItems().toMutableList()  // 既存の項目を取得
        items.add(newItem)  // 新しいアイテムを追加
        val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
        val editor = sharedPreferences.edit()
        editor.putString(ROULETTE_PREF_KEY_WEEKEND, itemsString)
        editor.apply()
    }

    // 平日のルーレット項目を追加して保存（Stringを引数に取る）
    fun saveWeekdayRouletteItems(newItem: String) {
        val items = getWeekdayRouletteItems().toMutableList()  // 既存の項目を取得
        items.add(newItem)  // 新しいアイテムを追加
        val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
        val editor = sharedPreferences.edit()
        editor.putString(ROULETTE_PREF_KEY_WEEKDAY, itemsString)
        editor.apply()
    }

    // 休日のルーレット項目を削除
    fun removeWeekendRouletteItem(itemToRemove: String) {
        val items = getWeekendRouletteItems().toMutableList()  // 既存の項目を取得
        items.remove(itemToRemove)  // 指定したアイテムを削除
        val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
        val editor = sharedPreferences.edit()
        editor.putString(ROULETTE_PREF_KEY_WEEKEND, itemsString)
        editor.apply()
    }

    // 平日のルーレット項目を削除
    fun removeWeekdayRouletteItem(itemToRemove: String) {
        val items = getWeekdayRouletteItems().toMutableList()  // 既存の項目を取得
        items.remove(itemToRemove)  // 指定したアイテムを削除
        val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
        val editor = sharedPreferences.edit()
        editor.putString(ROULETTE_PREF_KEY_WEEKDAY, itemsString)
        editor.apply()
    }
}