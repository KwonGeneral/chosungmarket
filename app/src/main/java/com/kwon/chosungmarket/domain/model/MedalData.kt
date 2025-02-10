package com.kwon.chosungmarket.domain.model

import com.kwon.chosungmarket.common.types.MedalType

/**
 * MedalData: 메달 정보를 담당하는 데이터 클래스
 *
 * @property id 메달 고유 식별자 (클라이언트에서 생성)
 * @property type 메달 종류 (BRONZE, SILVER, GOLD, PLATINUM)
 * @property quizGroupId 메달을 획득한 퀴즈 그룹 ID
 * @property acquiredAt 획득 시간 (서버 타임스탬프)
 */
data class MedalData(
    val id: String,
    val type: MedalType,
    val quizGroupId: String,
    val acquiredAt: Long = System.currentTimeMillis()
)