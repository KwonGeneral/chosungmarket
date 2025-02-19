package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.first

/**
 * 퀴즈 그룹 삭제를 담당하는 UseCase
 * 퀴즈 그룹을 삭제합니다.
 */
class DeleteQuizGroupUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) {
    suspend fun invoke(quizGroupId: String): Result<Unit> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("로그인이 필요합니다."))

        return quizRepositoryImpl.deleteQuizGroup(quizGroupId)
    }
}