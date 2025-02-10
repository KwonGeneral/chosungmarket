package com.kwon.chosungmarket.presenter.route

import com.kwon.chosungmarket.common.utils.KLog

sealed class CmRouter(val route: String) {
    data object Home : CmRouter("/home")
    data object Login : CmRouter("/login")
    data object MyInfo : CmRouter("/myinfo")
    data object Notification : CmRouter("/notification")
    data object HallOfFame : CmRouter("/halloffame")
    data object Setting : CmRouter("/setting")
    data object QuizGame : CmRouter("/quiz/game/{quizId}") {
        fun createRoute(quizId: String) = "/quiz/game/$quizId"
    }
    data object QuizResult : CmRouter("/quiz/result/{quizId}") {
        fun createRoute(quizId: String) = "/quiz/result/$quizId"
    }
    data object QuizDetail : CmRouter("/quiz/detail/{quizId}") {
        fun createRoute(quizId: String) = "/quiz/detail/$quizId"
    }

    data object QuizCreate : CmRouter("/quiz/create")

    companion object {
        fun fromRoute(route: String?): CmRouter? {
            return when {
                route == null -> null
                route == Home.route -> Home
                route == Login.route -> Login
                route == MyInfo.route -> MyInfo
                route == Notification.route -> Notification
                route == HallOfFame.route -> HallOfFame
                route == Setting.route -> Setting
                route == QuizCreate.route -> QuizCreate
                route.startsWith("/quiz/game/") -> QuizGame
                route.startsWith("/quiz/result/") -> QuizResult
                route.startsWith("/quiz/detail/") -> QuizDetail
                else -> {
                    null
                }
            }
        }
    }
}