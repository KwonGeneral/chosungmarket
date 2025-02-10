package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import kotlinx.coroutines.flow.Flow

interface QuizRepositoryImpl {
    suspend fun createQuiz(quiz: QuizData): Result<String>

    suspend fun createQuizGroup(quizGroup: QuizGroupData): Result<String>

    fun getQuizGroupList(
        limit: Int = 10,
        lastDocId: String? = null
    ): Flow<List<QuizGroupData>>

    suspend fun getQuizListByIdList(quizIdList: List<String>): Result<List<QuizData>>

    suspend fun toggleLike(quizGroupId: String): Result<Unit>

    suspend fun getQuizIdListByQuizGroup(quizGroupId: String): Result<List<String>>
}