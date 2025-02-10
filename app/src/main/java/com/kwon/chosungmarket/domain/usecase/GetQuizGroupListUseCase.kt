package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetQuizGroupListUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl
) {
    fun invoke(
        limit: Int = 10,
        lastDocId: String? = null
    ): Flow<List<QuizGroupData>> {
        return quizRepositoryImpl.getQuizGroupList(limit, lastDocId)
            .map { quizGroups ->
                quizGroups.sortedByDescending { it.createdAt }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
}