package com.example.roulettelife.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roulettelife.presentation.action.ActionScreen
import com.example.roulettelife.presentation.home.RouletteWeekdayScreen
import com.example.roulettelife.presentation.home.RouletteWeekendScreen
import com.example.roulettelife.presentation.map.MapScreen
import com.example.roulettelife.presentation.rouletteSettings.RouletteWeekdaySettingsScreen
import com.example.roulettelife.presentation.rouletteSettings.RouletteWeekendSettingsScreen
import com.google.android.gms.maps.model.LatLng

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
                onMapButtonClick = { navController.navigate(Screens.MAP.route) }
            )
        }

        // Map画面
        composable(Screens.MAP.route) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 35.6895 // デフォルトは東京
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 139.6917

            // 位置情報をLatLng型に変換
            val location = LatLng(latitude, longitude)

            // MapScreenに位置情報を渡す
            MapScreen(currentPosition = location)
        }
    }
}