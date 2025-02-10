package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.QuizGroupStatus
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTopQuizListUseCase(
    private val hallOfFameRepositoryImpl: HallOfFameRepositoryImpl
) {
    fun invoke(): Flow<List<QuizGroupData>> =
        hallOfFameRepositoryImpl.getTopQuizGroupList()
            .map { quizGroups ->
                quizGroups.filter { it.status == QuizGroupStatus.ACTIVE }
            }
}
