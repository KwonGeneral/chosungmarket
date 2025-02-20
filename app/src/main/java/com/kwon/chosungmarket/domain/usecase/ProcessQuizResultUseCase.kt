package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.ResultStatus
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * 퀴즈 결과 처리를 담당하는 UseCase
 * 사용자의 답안을 채점하고 결과를 저장합니다.
 */
class ProcessQuizResultUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val quizResultRepositoryImpl: QuizResultRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl
) {
    /**
     * 퀴즈 결과를 처리합니다.
     *
     * @param quizGroupId 퀴즈 그룹 ID
     * @param userAnswerList 사용자가 입력한 답안 목록
     * @return 생성된 퀴즈 결과 ID
     */
    suspend fun invoke(quizGroupId: String, userAnswerList: List<String>): Result<String> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            val quizIdList = quizRepositoryImpl.getQuizIdListByQuizGroup(quizGroupId)
                .getOrElse { return Result.failure(it) }

            val quizzes = quizRepositoryImpl.getQuizListByIdList(quizIdList).getOrThrow()
            val score = calculateScore(quizzes.map { it.answer }, userAnswerList)

            val quizResult = QuizResultData(
                id = UUID.randomUUID().toString(),
                userId = userId,
                quizGroupId = quizGroupId,
                score = score,
                answerList = userAnswerList,
                status = ResultStatus.VERIFIED,
                completedAt = System.currentTimeMillis()
            )

            quizResultRepositoryImpl.saveQuizResult(quizResult)
                .onSuccess { resultId ->
                    // user의 quizResultIdList에 결과 ID 추가
                    userRepositoryImpl.addQuizResultToUser(userId, resultId)
                        .getOrThrow()
                }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 점수를 계산합니다.
     *
     * @param correctAnswerList 정답 목록
     * @param userAnswerList 사용자 답안 목록
     * @return 100점 만점 기준 점수
     */
    private fun calculateScore(correctAnswerList: List<String>, userAnswerList: List<String>): Int {
        val correctCount = correctAnswerList.zip(userAnswerList)
            .count { (correct, user) ->
                correct.equals(user, ignoreCase = true)
            }

        return ((correctCount.toFloat() / correctAnswerList.size) * 100).toInt()
    }
}