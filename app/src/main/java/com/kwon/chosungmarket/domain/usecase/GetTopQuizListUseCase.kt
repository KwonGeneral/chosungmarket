package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.QuizGroupStatus
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 상위 랭킹 퀴즈 그룹 목록을 가져오는 UseCase
 */
class GetTopQuizListUseCase(
    private val hallOfFameRepositoryImpl: HallOfFameRepositoryImpl
) {
    fun invoke(): Flow<List<QuizGroupData>> =
        hallOfFameRepositoryImpl.getTopQuizGroupList()
            .map { quizGroups ->
                quizGroups.filter { it.status == QuizGroupStatus.ACTIVE }
            }
}
