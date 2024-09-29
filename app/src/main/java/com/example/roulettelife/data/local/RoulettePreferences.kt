package com.example.roulettelife.data.local

// SharedPreferencesでルーレットの目を管理
import android.content.Context
import android.content.SharedPreferences

class RoulettePreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val ROULETTE_PREF_KEY = "roulette_items"
        private const val PREFERENCES_NAME = "roulette_preferences"
    }

    // ルーレットの項目を取得
    fun getRouletteItems(): List<String> {
        val itemsString = sharedPreferences.getString(ROULETTE_PREF_KEY, "") ?: ""
        return if (itemsString.isNotBlank()) {
            itemsString.split(",")  // カンマ区切りの文字列をリストに変換
        } else {
            emptyList()
        }
    }

    // ルーレットの項目を保存
    fun saveRouletteItems(items: List<String>) {
        val editor = sharedPreferences.edit()
        val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
        editor.putString(ROULETTE_PREF_KEY, itemsString)
        editor.apply()
    }

    // ルーレットの項目を追加
    fun addRouletteItem(newItem: String) {
        val items = getRouletteItems().toMutableList()
        items.add(newItem)
        saveRouletteItems(items)
    }

    // ルーレットの項目を削除
    fun removeRouletteItem(itemToRemove: String) {
        val items = getRouletteItems().toMutableList()
        items.remove(itemToRemove)
        saveRouletteItems(items)
    }
}