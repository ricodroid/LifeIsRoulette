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
import com.example.roulettelife.presentation.home.HomeScreen
import com.example.roulettelife.presentation.rouletteSettings.RouletteSettingsScreen


@OptIn(ExperimentalMaterial3Api::class)
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
        startDestination = Screens.HOME.route,  // 最初のルートを指定
        modifier = modifier  // Modifierを設定
    ) {
        // Home画面
        composable(Screens.HOME.route) {
            HomeScreen(
                onSettingButtonClick = { navController.navigate(Screens.ROULETTE_SETTINGS.route) }
            )
        }

        // ルーレット設定画面
        composable(Screens.ROULETTE_SETTINGS.route) {
            RouletteSettingsScreen()
        }
    }
}