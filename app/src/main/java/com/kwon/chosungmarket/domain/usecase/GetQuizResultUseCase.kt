package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.presenter.page.QuizAnswer
import kotlinx.coroutines.flow.first

class GetQuizResultUseCase(
    private val quizResultRepositoryImpl: QuizResultRepositoryImpl,
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) {
    suspend fun invoke(resultId: String): Result<QuizResultData> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("User not logged in"))

        return quizResultRepositoryImpl.getQuizResult(resultId)
            .map { quizResult ->
                quizResult ?: throw Exception("결과를 찾을 수 없습니다.")
            }
    }

    suspend fun getQuizAnswerDetails(quizResult: QuizResultData): List<QuizAnswer> {
        val quizIdList = quizRepositoryImpl.getQuizIdListByQuizGroup(quizResult.quizGroupId)
            .getOrNull() ?: return emptyList()

        val quizzes = quizRepositoryImpl.getQuizListByIdList(quizIdList).getOrNull() ?: return emptyList()

        return quizzes.mapIndexed { index, quiz ->
            QuizAnswer(
                question = "문제 ${index + 1}",
                correctAnswer = quiz.answer,
                userAnswer = quizResult.answerList.getOrNull(index) ?: "",
                isCorrect = quiz.answer.equals(quizResult.answerList.getOrNull(index), ignoreCase = true),
                hint = quiz.description
            )
        }
    }
}