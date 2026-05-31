package com.pausiar.fit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pausiar.fit.ui.dashboard.DashboardScreen
import com.pausiar.fit.ui.day.DayScreen
import com.pausiar.fit.ui.edit.EditRoutineScreen
import com.pausiar.fit.ui.history.HistoryScreen
import com.pausiar.fit.ui.settings.SettingsScreen

@Composable
fun FitNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onOpenDay = { dayId, date -> navController.navigate(Routes.day(dayId, date)) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenEdit = { navController.navigate(Routes.EDIT) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.DAY,
            arguments = listOf(
                navArgument("dayId") { type = NavType.IntType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getInt("dayId") ?: 1
            val date = backStackEntry.arguments?.getString("date").orEmpty()
            DayScreen(
                dayId = dayId,
                dateIso = date,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.EDIT) }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.EDIT) {
            EditRoutineScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
