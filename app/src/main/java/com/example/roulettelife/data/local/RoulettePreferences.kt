package com.example.roulettelife.data.local


import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.util.Locale

// SharedPreferencesでルーレットの目を管理
class RoulettePreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val ROULETTE_PREF_KEY_WEEKEND = "roulette_items_weekend"
        private const val ROULETTE_PREF_KEY_WEEKDAY = "roulette_items_weekday"
        private const val DELETED_DEFAULT_ITEMS_KEY = "deleted_default_items"
        private const val PREFERENCES_NAME = "roulette_preferences"
        private const val LANGUAGE_PREF_KEY = "app_language"
        private const val SPIN_DATES_KEY = "spin_dates"
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

    // 削除されたデフォルトアイテムを取得
    fun getDeletedDefaultItems(): Set<String> {
        return sharedPreferences.getStringSet(DELETED_DEFAULT_ITEMS_KEY, emptySet()) ?: emptySet()
    }

    // 削除されたデフォルトアイテムを保存
    fun saveDeletedDefaultItem(item: String) {
        val deletedItems = getDeletedDefaultItems().toMutableSet()
        deletedItems.add(item)
        sharedPreferences.edit().putStringSet(DELETED_DEFAULT_ITEMS_KEY, deletedItems).apply()
    }

    // 削除されたデフォルトアイテムを復元
    fun removeDeletedDefaultItem(item: String) {
        val deletedItems = getDeletedDefaultItems().toMutableSet()
        deletedItems.remove(item)
        sharedPreferences.edit().putStringSet(DELETED_DEFAULT_ITEMS_KEY, deletedItems).apply()
    }

    // アイテムを保存する共通関数
    private fun saveItems(key: String, items: List<String>) {
        val itemsString = items.joinToString(",")
        sharedPreferences.edit().putString(key, itemsString).apply()
    }

    // 言語を保存する
    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(LANGUAGE_PREF_KEY, language).apply()
    }

    // 言語を取得する
    fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_PREF_KEY, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    // ルーレットを回した日付を取得する
    fun getRouletteSpinDates(): List<LocalDate> {
        val datesString = sharedPreferences.getString(SPIN_DATES_KEY, "") ?: ""
        return if (datesString.isNotBlank()) {
            datesString.split(",").map { LocalDate.parse(it) }
        } else {
            emptyList()
        }
    }

    // ルーレットを回した日付を保存する
    fun saveRouletteSpinDate(date: LocalDate) {
        val existingDates = getRouletteSpinDates().toMutableList()
        if (!existingDates.contains(date)) {  // 同じ日付が重複しないように
            existingDates.add(date)
        }
        val datesString = existingDates.joinToString(",") { it.toString() }  // LocalDate を文字列に変換して保存
        sharedPreferences.edit().putString(SPIN_DATES_KEY, datesString).apply()
    }
}