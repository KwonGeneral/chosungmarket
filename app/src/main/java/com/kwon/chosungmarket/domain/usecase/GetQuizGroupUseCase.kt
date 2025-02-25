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
) {
    /**
     * 퀴즈 그룹을 가져옵니다.
     *
     * @param quizGroupId 퀴즈 그룹 ID
     * @return 퀴즈 그룹과 그룹에 속한 퀴즈 목록
     */
    suspend fun invoke(quizGroupId: String): Result<Pair<QuizGroupData, List<QuizData>>> {
        return try {
            // 퀴즈 그룹 조회
            val quizGroup = quizRepositoryImpl.getQuizGroup(quizGroupId)
                .getOrElse {
                    return Result.failure(Exception("퀴즈 그룹을 찾을 수 없습니다."))
                }

            // 퀴즈 ID 목록으로 퀴즈들 조회
            val quizIdList = quizGroup.quizIdList
            val quizzes = quizRepositoryImpl.getQuizListByIdList(quizIdList)
                .getOrElse {
                    return Result.failure(Exception("퀴즈를 찾을 수 없습니다."))
                }

            Result.success(quizGroup to quizzes)
        } catch (e: Exception) {
            Result.failure(Exception("퀴즈 조회 중 오류가 발생했습니다: ${e.message}"))
        }
    }
}