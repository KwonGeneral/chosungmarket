package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl

/**
 * 퀴즈 좋아요 토글 UseCase
 */
class ToggleQuizLikeUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) {
    suspend fun invoke(quizGroupId: String): Result<Unit> {
        return quizRepositoryImpl.toggleLike(quizGroupId)
    }
}