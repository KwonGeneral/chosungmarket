package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 퀴즈 그룹 목록을 가져오는 UseCase
 */
class GetQuizGroupListUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl
) {
    /**
     * 퀴즈 그룹 목록을 가져옵니다.
     *
     * @param limit 가져올 개수
     * @param lastDocId 마지막 문서 ID
     * @return 퀴즈 그룹 목록
     */
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