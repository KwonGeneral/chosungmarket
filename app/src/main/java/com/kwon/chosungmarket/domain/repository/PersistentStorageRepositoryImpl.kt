package com.kwon.chosungmarket.domain.repository

import kotlinx.coroutines.flow.Flow

interface PersistentStorageRepositoryImpl {
    suspend fun saveKakaoId(kakaoId: String): Result<Unit>

    fun observeKakaoId(): Flow<String?>

    suspend fun clearKakaoId(): Result<Unit>

    suspend fun setAutoLoginEnabled(enabled: Boolean): Result<Unit>

    fun observeAutoLoginEnabled(): Flow<Boolean>
}