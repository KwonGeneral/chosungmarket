package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.FirebaseQuizResultsDb
import com.kwon.chosungmarket.data.mapper.QuizResultMapper
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first

class QuizResultRepository(
    private val firebaseQuizResultsDb: FirebaseQuizResultsDb,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) : QuizResultRepositoryImpl {
    override suspend fun saveQuizResult(quizResult: QuizResultData): Result<String> {
        return try {
            val resultData = QuizResultMapper.toFirestore(quizResult)
            val resultId = firebaseQuizResultsDb.createQuizResult(quizResult.userId, resultData)
            Result.success(resultId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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