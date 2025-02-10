package com.kwon.chosungmarket.presenter.route

/**
 * 앱의 라우팅을 정의하는 sealed class
 * 각 화면의 경로와 필요한 파라미터를 정의합니다.
 */
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
        /**
         * 문자열 경로를 CmRouter 객체로 변환합니다.
         * @param route 변환할 경로 문자열
         * @return 해당하는 CmRouter 객체 또는 null
         */
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