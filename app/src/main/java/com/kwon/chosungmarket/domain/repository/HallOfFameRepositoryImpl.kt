package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.common.types.MedalType
import com.kwon.chosungmarket.domain.model.QuizGroupData
import kotlinx.coroutines.flow.Flow

/**
 * 명예의 전당 기능에 필요한 데이터 액세스를 정의하는 인터페이스
 */
interface HallOfFameRepositoryImpl {
    /** 상위 랭킹 퀴즈 그룹 목록을 조회합니다. */
    fun getTopQuizGroupList(): Flow<List<QuizGroupData>>

    /**
     * 사용자에게 메달을 수여합니다.
     *
     * @param userId 수여 대상 사용자 ID
     * @param medalType 수여할 메달 종류
     */
    suspend fun awardMedal(userId: String, medalType: MedalType): Result<Unit>
}