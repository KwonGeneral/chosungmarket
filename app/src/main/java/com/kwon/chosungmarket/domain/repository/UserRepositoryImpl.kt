package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.model.UserData
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 관련 데이터 액세스를 정의하는 인터페이스
 */
interface UserRepositoryImpl {
    /** 카카오 계정으로 로그인합니다. */
    suspend fun signInWithKakao(kakaoId: String): Result<Boolean>

    /** 사용자 프로필을 업데이트합니다. */
    suspend fun updateUserProfile(
        userId: String,
        nickname: String,
        profileImageId: Int,
        image: String
    ): Result<Unit>

    /** 현재 로그인한 사용자 정보를 관찰합니다. */
    fun getCurrentUser(): Flow<UserData?>

    /** 회원 탈퇴를 처리합니다. */
    suspend fun withdrawUser(userId: String): Result<Unit>

    /** 사용자의 퀴즈 그룹 목록 추가 */
    suspend fun addQuizGroupToUser(userId: String, quizGroupId: String): Result<Unit>

    /** 사용자의 퀴즈 결과 추가 */
    suspend fun addQuizResultToUser(userId: String, resultId: String): Result<Unit>

    /** 사용자의 퀴즈 그룹 목록 제거 */
    suspend fun removeQuizGroupFromUser(userId: String, quizGroupId: String): Result<Unit>

    /** 사용자의 포인트 업데이트 */
    suspend fun updateUserPoint(userId: String, pointToAdd: Int): Result<Unit>
}