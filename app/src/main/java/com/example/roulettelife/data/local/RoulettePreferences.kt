package com.example.roulettelife.data.local

// SharedPreferencesでルーレットの目を管理
import android.content.Context

// ルーレットの項目を保存するためのキー
private const val ROULETTE_PREF_KEY = "roulette_items"
private const val PREFERENCES_NAME = "roulette_preferences"

// SharedPreferences からルーレットの項目を取得
fun getRouletteItems(context: Context): List<String> {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val itemsString = sharedPreferences.getString(ROULETTE_PREF_KEY, "") ?: ""
    return if (itemsString.isNotBlank()) {
        itemsString.split(",")  // 保存されている文字列をカンマで分割してリストに変換
    } else {
        emptyList()
    }
}

// ルーレットの項目を SharedPreferences に保存
fun saveRouletteItems(context: Context, items: List<String>) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val itemsString = items.joinToString(",")  // リストをカンマ区切りの文字列に変換
    editor.putString(ROULETTE_PREF_KEY, itemsString)
    editor.apply()  // 非同期で保存
}

// ルーレットの項目を追加
fun addRouletteItem(context: Context, newItem: String) {
    val items = getRouletteItems(context).toMutableList()
    items.add(newItem)
    saveRouletteItems(context, items)
}

// ルーレットの項目を削除
fun removeRouletteItem(context: Context, itemToRemove: String) {
    val items = getRouletteItems(context).toMutableList()
    items.remove(itemToRemove)
    saveRouletteItems(context, items)
}