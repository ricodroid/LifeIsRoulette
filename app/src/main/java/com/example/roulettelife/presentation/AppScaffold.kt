package com.example.roulettelife.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roulettelife.presentation.action.ActionScreen
import com.example.roulettelife.presentation.diary.DiaryScreen
import com.example.roulettelife.presentation.home.RouletteWeekdayScreen
import com.example.roulettelife.presentation.home.RouletteWeekendScreen
import com.example.roulettelife.presentation.rouletteSettings.RouletteWeekdaySettingsScreen
import com.example.roulettelife.presentation.rouletteSettings.RouletteWeekendSettingsScreen

@Composable
fun AppScaffold() {
    val navController = rememberNavController()

    Scaffold { padding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(padding))
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,  // NavHostControllerを使用
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,  // navControllerを指定
        startDestination = Screens.ROULETTE_WEEKEND.route,  // 最初のルートを指定
        modifier = modifier  // Modifierを設定
    ) {
        // Home画面1
        composable(Screens.ROULETTE_WEEKEND.route) {
            RouletteWeekendScreen(
                navController = navController,
                onSettingButtonClick = { navController.navigate(Screens.ROULETTE_WEEKEND_SETTINGS.route) },
                onChangeRouletteButtonClick = { navController.navigate(Screens.ROULETTE_WEEKDAY.route) }
            )
        }

        // Home画面2
        composable(Screens.ROULETTE_WEEKDAY.route) {
            RouletteWeekdayScreen(
                navController = navController,
                onSettingButtonClick = { navController.navigate(Screens.ROULETTE_WEEKDAY_SETTINGS.route) },
                onChangeRouletteButtonClick = { navController.navigate(Screens.ROULETTE_WEEKEND.route) }
            )
        }

        // ルーレット週末設定画面
        composable(Screens.ROULETTE_WEEKEND_SETTINGS.route) {
            RouletteWeekendSettingsScreen(
                onHomeButtonClick = { navController.navigate(Screens.ROULETTE_WEEKDAY.route) },
                onChangeWeekendButtonClick = { navController.navigate(Screens.ROULETTE_WEEKDAY_SETTINGS.route) }
            )
        }

        // ルーレット平日設定画面
        composable(Screens.ROULETTE_WEEKDAY_SETTINGS.route) {
            RouletteWeekdaySettingsScreen(
                onHomeButtonClick = { navController.navigate(Screens.ROULETTE_WEEKDAY.route) },
                onChangeWeekendButtonClick = { navController.navigate(Screens.ROULETTE_WEEKEND_SETTINGS.route) }
            )
        }

        // アクション画面
        composable("${Screens.ACTION.route}/{selectedItem}") { backStackEntry ->
            val selectedItem = backStackEntry.arguments?.getString("selectedItem") ?: ""
            ActionScreen(
                selectedItem = selectedItem,
                navController = navController,
                onPhotoSaved = { uri, diaryEntry ->
                    // 写真保存後にDiary画面へ遷移
                    navController.navigate(Screens.DIARY.createRoute(uri.toString(), diaryEntry))
                }
            )
        }

        // 日記画面 (写真のURIと日記の内容を受け取る)
        composable(
            route = Screens.DIARY.route,
            arguments = listOf(
                navArgument("photoUri") { type = NavType.StringType },
                navArgument("diaryEntry") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoUri = Uri.decode(backStackEntry.arguments?.getString("photoUri") ?: "")
            val diaryEntry = Uri.decode(backStackEntry.arguments?.getString("diaryEntry") ?: "")

            DiaryScreen(
                navController = navController,
                photoUri = photoUri,
                diaryEntry = diaryEntry
            )
        }
    }
}