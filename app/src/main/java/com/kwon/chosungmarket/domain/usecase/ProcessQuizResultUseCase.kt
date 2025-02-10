package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.ResultStatus
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.first
import java.util.UUID

class ProcessQuizResultUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val quizResultRepositoryImpl: QuizResultRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) {
    suspend fun invoke(quizGroupId: String, userAnswerList: List<String>): Result<String> {
        return try {
            val quizIdList = quizRepositoryImpl.getQuizIdListByQuizGroup(quizGroupId)
                .getOrElse { return Result.failure(it) }

            val quizzes = quizRepositoryImpl.getQuizListByIdList(quizIdList).getOrThrow()

            val correctAnswerList = quizzes.map { it.answer }
            val score = calculateScore(correctAnswerList, userAnswerList)

            val quizResult = QuizResultData(
                id = UUID.randomUUID().toString(),
                userId = sessionRepositoryImpl.getUserId().first()!!,
                quizGroupId = quizGroupId,
                score = score,
                answerList = userAnswerList,
                status = ResultStatus.VERIFIED,
                completedAt = System.currentTimeMillis()
            )

            quizResultRepositoryImpl.saveQuizResult(quizResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateScore(correctAnswerList: List<String>, userAnswerList: List<String>): Int {
        val correctCount = correctAnswerList.zip(userAnswerList)
            .count { (correct, user) ->
                correct.equals(user, ignoreCase = true)
            }

        return ((correctCount.toFloat() / correctAnswerList.size) * 100).toInt()
    }
}