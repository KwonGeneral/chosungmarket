package com.kwon.chosungmarket.common.types

/**
 * ResultStatus: 퀴즈 결과 상태를 정의하는 열거형 클래스
 *
 * @property PENDING 검수 대기 중
 * @property VERIFIED 검수 완료
 * @property REJECTED 거부됨
 */
enum class ResultStatus {
    PENDING, VERIFIED, REJECTED
}