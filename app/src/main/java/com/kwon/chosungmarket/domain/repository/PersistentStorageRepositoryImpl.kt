package com.kwon.chosungmarket.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * 로컬 저장소 접근을 정의하는 인터페이스
 * 자동 로그인 등 앱 설정 데이터를 관리합니다.
 */
interface PersistentStorageRepositoryImpl {
    /**
     * 카카오 ID를 저장합니다.
     * 자동 로그인에 사용됩니다.
     */
    suspend fun saveKakaoId(kakaoId: String): Result<Unit>

    /** 저장된 카카오 ID를 관찰합니다. */
    fun getKakaoId(): Flow<String?>

    /** 저장된 카카오 ID를 삭제합니다. */
    suspend fun clearKakaoId(): Result<Unit>

    /** 자동 로그인 설정을 변경합니다. */
    suspend fun setAutoLoginEnabled(enabled: Boolean): Result<Unit>

    /** 자동 로그인 설정을 관찰합니다. */
    fun getAutoLoginEnabled(): Flow<Boolean>
}