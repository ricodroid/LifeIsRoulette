package com.example.roulettelife.data.local


import android.content.Context
import android.content.SharedPreferences

class DiaryPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "diary_preferences"
    }

    // 日記を取得（写真のURIに紐づく）
    fun getDiary(photoUri: String): String? {
        return sharedPreferences.getString(photoUri, null)  // URIに紐づく日記を取得
    }

    // 日記を保存（写真のURIと日記の内容を引数に取る）
    fun saveDiary(photoUri: String, diaryEntry: String) {
        val editor = sharedPreferences.edit()
        editor.putString(photoUri, diaryEntry)  // URIをキーにして日記を保存
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