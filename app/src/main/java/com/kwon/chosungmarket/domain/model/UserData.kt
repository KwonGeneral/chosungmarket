package com.kwon.chosungmarket.domain.model

/**
 * UserData: 사용자 정보를 담당하는 데이터 클래스
 *
 * @property id Firebase 문서 ID (users 컬렉션의 문서 ID)
 * @property kakaoId 카카오 소셜 로그인 ID (고유값)
 * @property nickname 사용자 닉네임
 * @property profileImageId 프로필 이미지 ID (제한된 이미지 중 선택)
 * @property point 사용자가 획득한 총 포인트 (퀴즈 1문제당 1점)
 * @property medalList 사용자가 획득한 메달 목록
 * @property createdAt 계정 생성 시간 (서버 타임스탬프)
 * @property updatedAt 마지막 정보 수정 시간 (서버 타임스탬프)
 * @property lastLoginAt 마지막 로그인 시간 (서버 타임스탬프)
 */
data class UserData(
    val id: String,
    val kakaoId: String,
    val nickname: String,
    val profileImageId: Int,
    val point: Int = 0,
    val medalList: List<MedalData> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)