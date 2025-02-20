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
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val getQuizResultCountUseCase: GetQuizResultCountUseCase
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
                quizGroups.map { quizGroup ->
                    // 각 퀴즈 그룹의 결과 카운트를 조회하여 추가
                    val resultCount = getQuizResultCountUseCase.invoke(quizGroup.id)
                        .getOrDefault(0)

                    // 새로운 퀴즈 그룹 데이터 생성 (quizResultCount 포함)
                    quizGroup.copy(quizResultCount = resultCount, userNickname = quizGroup.userNickname.ifBlank { "익명" })
                }
                    .sortedByDescending { it.createdAt }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
}