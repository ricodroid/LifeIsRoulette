package com.example.roulettelife.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roulettelife.R
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

    // 各月の1日〜15日と16日〜月末をリストに追加
    val firstHalfDays = mutableListOf<List<LocalDate>>()
    val secondHalfDays = mutableListOf<List<LocalDate>>()

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
        firstHalfDays.add(firstHalf)

        // 各月の16日〜月末
        val lastDayOfMonth = YearMonth.of(year, month).lengthOfMonth()
        val secondHalf = (16..lastDayOfMonth).mapNotNull { day ->
            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                null // 無効な日付は無視
            }
        }
        secondHalfDays.add(secondHalf)
    }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Column {
        // ヘッダー行に月名を表示
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            monthNames.forEach { month ->
                Text(
                    text = month,
                    fontFamily = FontFamily(Font(R.font.round_text, FontWeight.ExtraBold)),
                    modifier = Modifier.weight(1f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // グリッドの表示
        LazyVerticalGrid(
            columns = GridCells.Fixed(12),  // 12列で表示
            modifier = Modifier
                .fillMaxWidth()
                ,
            content = {
                // 各月の前半（1日〜15日）を1列目〜12列目に表示
                firstHalfDays.forEachIndexed { monthIndex, daysInFirstHalf ->
                    // この月の前半でスピンされた日を数える
                    val spunCountFirstHalf = daysInFirstHalf.count { it in spinDateSet }
                    val colorRatioFirstHalf = spunCountFirstHalf / daysInFirstHalf.size.toFloat()
                    val cellColorFirstHalf = lerp(Color.White, Color(0xFFFFA500), colorRatioFirstHalf)

                    item {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .aspectRatio(1f)  // 正方形に設定
                                .background(cellColorFirstHalf, shape = RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
//                            Text(text = "${monthIndex + 1}月 前半", fontSize = 12.sp)
                        }
                    }
                }

                // 各月の後半（16日〜月末）を1列目〜12列目の次の行に表示
                secondHalfDays.forEachIndexed { monthIndex, daysInSecondHalf ->
                    // この月の後半でスピンされた日を数える
                    val spunCountSecondHalf = daysInSecondHalf.count { it in spinDateSet }
                    val colorRatioSecondHalf = spunCountSecondHalf / daysInSecondHalf.size.toFloat()
                    val cellColorSecondHalf = lerp(Color.White, Color(0xFFFFA500), colorRatioSecondHalf)

                    item {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .aspectRatio(1f)  // 正方形に設定
                                .background(cellColorSecondHalf, shape = RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
//                            Text(text = "${monthIndex + 1}月 後半", fontSize = 12.sp)
                        }
                    }
                }
            }
        )
    }
}
