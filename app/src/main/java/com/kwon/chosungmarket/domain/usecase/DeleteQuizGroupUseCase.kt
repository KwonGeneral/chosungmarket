package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

/**
 * 퀴즈 그룹 삭제를 담당하는 UseCase
 * 퀴즈 그룹을 삭제합니다.
 */
class DeleteQuizGroupUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl
) {
    suspend fun invoke(quizGroupId: String): Result<Unit> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("로그인이 필요합니다."))

        return try {
            // 퀴즈 그룹 소유자 확인
            val quizGroup = quizRepositoryImpl.getQuizGroup(quizGroupId)
                .getOrElse { return Result.failure(Exception("퀴즈 그룹을 찾을 수 없습니다.")) }

            if (quizGroup.userId != userId) {
                return Result.failure(Exception("퀴즈 그룹을 삭제할 권한이 없습니다."))
            }

            // 퀴즈 그룹 삭제
            quizRepositoryImpl.deleteQuizGroup(quizGroupId)
                .onSuccess {
                    userRepositoryImpl.removeQuizGroupFromUser(userId, quizGroupId)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}