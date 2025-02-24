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
    private val hallOfFameRepositoryImpl: HallOfFameRepositoryImpl,
    private val getQuizResultCountUseCase: GetQuizResultCountUseCase
) {
    fun invoke(): Flow<List<QuizGroupData>> =
        hallOfFameRepositoryImpl.getTopQuizGroupList()
            .map { quizGroups ->
                quizGroups.filter { it.status == QuizGroupStatus.ACTIVE }
                quizGroups.map { quizGroup ->
                    // 각 퀴즈 그룹의 결과 카운트를 조회하여 추가
                    val resultCount = getQuizResultCountUseCase.invoke(quizGroup.id)
                        .getOrDefault(0)

                    // 새로운 퀴즈 그룹 데이터 생성 (quizResultCount 포함)
                    quizGroup.copy(quizResultCount = resultCount, userNickname = quizGroup.userNickname.ifBlank { "익명" })
                }
                    .sortedByDescending { it.createdAt }
            }
}
