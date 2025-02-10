package com.kwon.chosungmarket.domain.model

import com.kwon.chosungmarket.common.types.ResultStatus

/**
 * QuizResultData: 퀴즈 결과 정보를 담당하는 데이터 클래스
 *
 * @property id Firebase 문서 ID
 * @property userId 퀴즈 푼 사용자 ID
 * @property quizGroupId 퀴즈 그룹 ID
 * @property score 획득 점수
 * @property answerList 사용자가 입력한 답안 목록
 * @property completedAt 완료 시간 (서버 타임스탬프)
 * @property status 결과 상태 (PENDING, VERIFIED, REJECTED)
 */
data class QuizResultData(
    val id: String,
    val userId: String,
    val quizGroupId: String,
    val score: Int,
    val answerList: List<String>,
    val completedAt: Long = System.currentTimeMillis(),
    val status: ResultStatus = ResultStatus.PENDING
)