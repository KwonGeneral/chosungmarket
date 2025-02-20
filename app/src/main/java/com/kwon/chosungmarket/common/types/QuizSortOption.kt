package com.kwon.chosungmarket.common.types

/**
 * QuizSortOption: 퀴즈 정렬 옵션을 정의하는 열거형 클래스
 *
 * @property RECOMMENDED 추천순 (좋아요 수 기준)
 * @property NEWEST 최신순
 * @property OLDEST 오래된순
 */
enum class QuizSortOption {
    RECOMMENDED,
    NEWEST,
    OLDEST
}