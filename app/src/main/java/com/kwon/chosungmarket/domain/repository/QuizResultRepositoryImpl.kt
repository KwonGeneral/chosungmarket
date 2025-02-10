package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.QuizResultData
import kotlinx.coroutines.flow.Flow

interface QuizResultRepositoryImpl {
    suspend fun saveQuizResult(quizResult: QuizResultData): Result<String>

    suspend fun getQuizResult(resultId: String): Result<QuizResultData?>

    fun getQuizResultList(limit: Int = 10): Flow<List<QuizResultData>>

    suspend fun deleteQuizResult(resultId: String): Result<Unit>
}