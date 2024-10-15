package com.example.roulettelife.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.roulettelife.data.local.RoulettePreferences
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun RouletteSpinCalendarScreen() {
    val context = LocalContext.current
    val roulettePreferences = remember { RoulettePreferences(context) }

    // 今日の日付
    val today = LocalDate.now()

    // ルーレットを回した日を取得
    val spinDates = remember {
        val dates = roulettePreferences.getRouletteSpinDates()
        Log.d("RouletteSpinCalendarScreen", "Spin dates: $dates")
        dates
    }
    val spinDateSet = spinDates.toSet()

    // 各月の1日〜15日と16日〜月末に日付を分割してリストに追加
    val allDays = mutableListOf<List<LocalDate>>()
    for (month in 1..12) {
        val year = today.year
        // 各月の1日〜15日
        val firstHalf = (1..15).mapNotNull { day ->
            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                null // 無効な日付は無視
            }
        }
        allDays.add(firstHalf)

        // 各月の16日〜月末
        val lastDayOfMonth = YearMonth.of(year, month).lengthOfMonth()
        val secondHalf = (16..lastDayOfMonth).mapNotNull { day ->
            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                null // 無効な日付は無視
            }
        }
        allDays.add(secondHalf)
    }

    // グリッドの表示
    LazyVerticalGrid(
        columns = GridCells.Fixed(12),  // 12列で表示
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        content = {
            allDays.forEachIndexed { index, daysInGroup ->
                // このマスの中でスピンされた日数を数える
                val spunCount = daysInGroup.count { it in spinDateSet }

                // スピンされた日数に基づいて色を濃くする
                val colorRatio = spunCount / daysInGroup.size.toFloat()  // 日数に対するスピンされた割合
                val cellColor = lerp(Color.White, Color(0xFFFFA500), colorRatio)

                item {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)  // 正方形に設定
                            .background(cellColor, shape = RoundedCornerShape(4.dp)),
                    )
                }
            }
        }
    )
}

