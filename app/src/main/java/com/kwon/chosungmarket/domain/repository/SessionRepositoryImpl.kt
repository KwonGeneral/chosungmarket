package com.kwon.chosungmarket.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * 로그인 세션 관리를 정의하는 인터페이스
 */
interface SessionRepositoryImpl {
    /** 현재 로그인한 사용자의 ID를 저장합니다. */
    suspend fun saveUserId(userId: String): Result<Unit>

    /** 현재 로그인한 사용자의 ID를 관찰합니다. */
    fun getUserId(): Flow<String?>

    /** 로그인 세션을 종료합니다. */
    suspend fun clearUserId(): Result<Unit>
}