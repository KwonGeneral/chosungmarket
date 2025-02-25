package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.QuizSortOption
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 퀴즈 그룹 목록을 가져오는 UseCase
 */
class GetQuizGroupListUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val getQuizResultCountUseCase: GetQuizResultCountUseCase
) {
    /**
     * 퀴즈 그룹 목록을 가져옵니다.
     *
     * @param limit 가져올 개수
     * @param lastDocId 마지막 문서 ID
     * @param tag 퀴즈 그룹의 태그
     * @param sortOption 정렬 옵션
     */
    suspend fun invoke(
        limit: Int = 10,
        lastDocId: String? = null,
        tag: String? = null,
        sortOption: QuizSortOption = QuizSortOption.RECOMMENDED
    ): Flow<List<QuizGroupData>> {
        return quizRepositoryImpl.getQuizGroupList(limit, lastDocId, tag, sortOption)
            .map { quizGroups ->
                // 직접 정렬
                val sortedGroups = when (sortOption) {
                    QuizSortOption.RECOMMENDED -> quizGroups.sortedByDescending { it.likeCount }
                    QuizSortOption.NEWEST -> quizGroups.sortedByDescending { it.createdAt }
                    QuizSortOption.OLDEST -> quizGroups.sortedBy { it.createdAt }
                }

                // 나머지 처리
                sortedGroups.map { quizGroup ->
                    val resultCount = getQuizResultCountUseCase.invoke(quizGroup.id)
                        .getOrDefault(0)

                    quizGroup.copy(
                        quizResultCount = resultCount,
                        userNickname = quizGroup.userNickname.ifBlank { "익명" }
                    )
                }
            }
    }
}