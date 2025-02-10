package com.kwon.chosungmarket.domain.model

import com.kwon.chosungmarket.common.types.QuizDifficulty

/**
 * QuizData: 개별 초성 퀴즈 정보를 담당하는 데이터 클래스
 *
 * @property id 퀴즈 고유 식별자 (클라이언트에서 생성)
 * @property consonant 초성 문제
 * @property answer 정답
 * @property description 문제 설명 또는 힌트
 * @property tagList 퀴즈 관련 태그 목록
 * @property difficulty 문제 난이도 (EASY, MEDIUM, HARD)
 */
data class QuizData(
    val id: String,
    val consonant: String,
    val answer: String,
    val description: String,
    val tagList: List<String> = emptyList(),
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
)