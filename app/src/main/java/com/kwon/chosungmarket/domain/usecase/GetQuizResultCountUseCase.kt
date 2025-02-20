package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl

/**
 * 퀴즈 그룹의 퀴즈 결과 목록 갯수 가져오기를 담당하는 UseCase
 * 퀴즈 그룹의 퀴즈 결과 목록 갯수를 가져옵니다.
 */
class GetQuizResultCountUseCase(
    private val quizResultRepositoryImpl: QuizResultRepositoryImpl
) {
    suspend fun invoke(quizGroupId: String): Result<Int> {
        return quizResultRepositoryImpl.getQuizGroupResultCount(quizGroupId)
    }
}