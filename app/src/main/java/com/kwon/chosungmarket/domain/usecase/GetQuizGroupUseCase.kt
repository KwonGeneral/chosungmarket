package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.first

/**
 * 퀴즈 그룹을 가져오는 UseCase
 */
class GetQuizGroupUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) {
    /**
     * 퀴즈 그룹을 가져옵니다.
     *
     * @param quizGroupId 퀴즈 그룹 ID
     * @return 퀴즈 그룹과 그룹에 속한 퀴즈 목록
     */
    suspend fun invoke(quizGroupId: String): Result<Pair<QuizGroupData, List<QuizData>>> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            val quizGroup = quizRepositoryImpl.getQuizGroupList(limit = 100)
                .first()
                .find { it.id == quizGroupId }
                ?: return Result.failure(Exception("퀴즈 그룹을 찾을 수 없습니다."))

            val quizzes = quizRepositoryImpl.getQuizListByIdList(quizGroup.quizIdList)
                .getOrElse { return Result.failure(it) }

            Result.success(quizGroup to quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}