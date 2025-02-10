package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.QuizResultData
import kotlinx.coroutines.flow.Flow

/**
 * 퀴즈 결과 데이터 액세스를 정의하는 인터페이스
 */
interface QuizResultRepositoryImpl {
    /** 퀴즈 결과를 저장합니다. */
    suspend fun saveQuizResult(quizResult: QuizResultData): Result<String>

    /** 퀴즈 결과를 조회합니다. */
    suspend fun getQuizResult(resultId: String): Result<QuizResultData?>

    /** 퀴즈 결과 목록을 조회합니다. */
    fun getQuizResultList(limit: Int = 10): Flow<List<QuizResultData>>

    /** 퀴즈 결과를 삭제합니다. */
    suspend fun deleteQuizResult(resultId: String): Result<Unit>
}