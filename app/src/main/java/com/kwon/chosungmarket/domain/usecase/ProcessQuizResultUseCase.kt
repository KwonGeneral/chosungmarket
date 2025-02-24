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

            // 퀴즈 ID 목록 가져오기
            val quizIdList = quizRepositoryImpl.getQuizIdListByQuizGroup(quizGroupId)
                .getOrElse { return Result.failure(it) }

            // 퀴즈 데이터 가져오기
            val quizzes = quizRepositoryImpl.getQuizListByIdList(quizIdList)
                .getOrElse { return Result.failure(it) }

            // 정답 체크 및 점수 계산
            val correctAnswers = quizzes.zip(userAnswerList).count { (quiz, answer) ->
                quiz.answer.equals(answer, ignoreCase = true)
            }

            val quizResult = QuizResultData(
                id = UUID.randomUUID().toString(),
                userId = userId,
                quizGroupId = quizGroupId,
                score = ((correctAnswers.toFloat() / quizzes.size) * 100).toInt(),
                answerList = userAnswerList,
                completedAt = System.currentTimeMillis()
            )

            // 퀴즈 결과 저장
            val resultId = quizResultRepositoryImpl.saveQuizResult(quizResult).getOrThrow()

            // 유저 포인트 업데이트 (이전에 풀지 않은 퀴즈인 경우에만)
            val userData = userRepositoryImpl.getCurrentUser().first()
            if (userData != null && quizGroupId !in userData.quizResultIdList) {
                userRepositoryImpl.updateUserPoint(userId, correctAnswers)
                    .getOrThrow()
            }

            Result.success(resultId)
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