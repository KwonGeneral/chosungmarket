package com.kwon.chosungmarket.domain.model

import com.kwon.chosungmarket.common.types.QuizGroupStatus

/**
 * QuizGroupData: 초성 퀴즈 그룹 정보를 담당하는 데이터 클래스
 *
 * @property id Firebase 문서 ID
 * @property userId 퀴즈 그룹 생성자의 ID (users 컬렉션의 문서 ID)
 * @property title 퀴즈 그룹 제목
 * @property description 퀴즈 그룹 설명
 * @property quizIdList 퀴즈 IdList 목록
 * @property likeCount 추천 수
 * @property likedUserIdList 추천한 사용자 ID 목록 (빠른 조회를 위해 배열로 저장)
 * @property createdAt 생성 시간 (서버 타임스탬프)
 * @property updatedAt 수정 시간 (서버 타임스탬프)
 * @property userNickname 생성자 닉네임 (실시간 동기화 X)
 * @property status 퀴즈 그룹 상태 (ACTIVE, REPORTED, DELETED 등)
 * @property image 퀴즈 그룹 이미지 URL
 * @property tagList 퀴즈 그룹 태그 목록
 */
data class QuizGroupData(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val image: String = "",
    val tagList: List<String> = emptyList(),
    val quizIdList: List<String>,
    val likeCount: Int = 0,
    val likedUserIdList: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userNickname: String = "",
    val status: QuizGroupStatus = QuizGroupStatus.ACTIVE
)