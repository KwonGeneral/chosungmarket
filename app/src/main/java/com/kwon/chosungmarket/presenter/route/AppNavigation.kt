package com.kwon.chosungmarket.presenter.route

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.presenter.page.HallOfFamePage
import com.kwon.chosungmarket.presenter.page.HomePage
import com.kwon.chosungmarket.presenter.page.LoginPage
import com.kwon.chosungmarket.presenter.page.MyInfoPage
import com.kwon.chosungmarket.presenter.page.NotificationPage
import com.kwon.chosungmarket.presenter.page.QuizCreatePage
import com.kwon.chosungmarket.presenter.page.QuizDetailPage
import com.kwon.chosungmarket.presenter.page.QuizGamePage
import com.kwon.chosungmarket.presenter.page.QuizResultPage
import com.kwon.chosungmarket.presenter.page.SettingPage

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = CmRouter.Login.route,
    ) {
        composable(CmRouter.Home.route) {
            HomePage(navController)
        }
        composable(CmRouter.Login.route) {
            LoginPage(navController)
        }
        composable(CmRouter.MyInfo.route) {
            MyInfoPage(navController)
        }
        composable(CmRouter.Notification.route) {
            NotificationPage(navController)
        }
        composable(CmRouter.QuizCreate.route) {
            QuizCreatePage(navController)
        }
        composable(CmRouter.HallOfFame.route) {
            HallOfFamePage(navController)
        }
        composable(CmRouter.Setting.route) {
            SettingPage(navController)
        }
        composable(
            route = CmRouter.QuizGame.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            QuizGamePage(navController, quizId)
        }
        composable(
            route = CmRouter.QuizDetail.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            QuizDetailPage(navController, quizId)
        }
        composable(
            route = CmRouter.QuizResult.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            QuizResultPage(navController, quizId)
        }
    }
}

fun NavHostController.navigateTo(destination: String, args: Bundle? = null) {
    if (currentDestination?.route != destination) {
        navigate(destination)
        {
            // 이미 최상단에 해당 목적지가 있으면 새로 시작하지 않음
            launchSingleTop = true

            // 모든 테스크를 지운다.
            popUpTo(graph.startDestinationId) {
                saveState = true
                inclusive = false
            }

            restoreState = true
        }
    }
}

fun NavController.clearNavigateTo(destination: String) {
    if (currentDestination?.route != destination) {
        navigate(destination)
        {
            // 이미 최상단에 해당 목적지가 있으면 새로 시작하지 않음
            launchSingleTop = true

            popUpTo(graph.startDestinationId) {
                saveState = true
                inclusive = true
            }
        }
    }
}