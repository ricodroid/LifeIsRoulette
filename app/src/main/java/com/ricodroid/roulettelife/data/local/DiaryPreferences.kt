package com.ricodroid.roulettelife.data.local


import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "diary_preferences"
        private const val KEY_SELECTED_ITEM = "selected_item"
    }

    // 選択された項目を取得
    fun getSelectedItem(): String? {
        return sharedPreferences.getString(KEY_SELECTED_ITEM, null)
    }

    // 選択された項目を保存
    fun saveSelectedItem(item: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SELECTED_ITEM, item)
        editor.apply()
    }

    // 選択された項目を削除
    fun removeSelectedItem() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_SELECTED_ITEM)  // 選択された項目を削除
        editor.apply()
    }

    // 日記を取得（写真のURIに紐づく）
    fun getDiary(photoUri: String): String? {
        return sharedPreferences.getString(photoUri, null)  // URIに紐づく日記を取得
    }

    // 日記を保存（写真のURIと日記の内容を引数に取る）
    fun saveDiary(photoUri: String, diaryEntry: String) {
        val editor = sharedPreferences.edit()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // 現在の日付を取得
        val combinedEntry = "$diaryEntry\nDate: $currentDate"  // 日記と日付を結合して保存
        editor.putString(photoUri, combinedEntry)  // 日記に日付を追加
        editor.apply()
    }

    // 日記を削除（写真のURIに紐づく日記を削除）
    fun removeDiary(photoUri: String) {
        val editor = sharedPreferences.edit()
        editor.remove(photoUri)  // URIに紐づく日記を削除
        editor.apply()
    }

    // すべての日記を取得（全ての写真のURIに紐づく日記を取得）
    fun getAllDiaries(): Map<String, String> {
        return sharedPreferences.all.filterValues { it is String } as Map<String, String>
    }
}