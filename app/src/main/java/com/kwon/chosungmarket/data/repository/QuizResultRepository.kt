package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.FirebaseQuizResultsDb
import com.kwon.chosungmarket.data.mapper.QuizResultMapper
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 퀴즈 결과 관련 데이터 처리를 담당하는 Repository 구현체
 * 퀴즈 결과 저장, 조회, 삭제 등의 기능을 제공합니다.
 */
class QuizResultRepository(
    private val firebaseQuizResultsDb: FirebaseQuizResultsDb,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) : QuizResultRepositoryImpl {

    /** 퀴즈 결과를 저장합니다. */
    override suspend fun saveQuizResult(quizResult: QuizResultData): Result<String> {
        return try {
            val resultData = QuizResultMapper.toFirestore(quizResult)
            val resultId = firebaseQuizResultsDb.createQuizResult(quizResult.userId, resultData)
            Result.success(resultId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 퀴즈 결과를 조회합니다. */
    override suspend fun getQuizResult(resultId: String): Result<QuizResultData?> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            val resultData = firebaseQuizResultsDb.getQuizResult(userId, resultId)
            val quizResult = resultData?.let { QuizResultMapper.fromFirestore(resultId, it) }
            Result.success(quizResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 퀴즈 결과 목록을 조회합니다. */
    override fun getQuizResultList(limit: Int): Flow<List<QuizResultData>> = flow {
        try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: throw Exception("User not logged in")

            val results = firebaseQuizResultsDb.getUserQuizResults(userId, limit)
            val quizResults = results.map { resultData ->
                QuizResultMapper.fromFirestore(resultData["id"] as String, resultData)
            }
            emit(quizResults)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /** 퀴즈 결과를 삭제합니다. */
    override suspend fun deleteQuizResult(resultId: String): Result<Unit> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            firebaseQuizResultsDb.deleteQuizResult(userId, resultId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}