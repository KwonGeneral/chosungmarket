package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepositoryImpl {
    suspend fun signInWithKakao(kakaoId: String): Result<Boolean>

    suspend fun updateUserProfile(userId: String, nickname: String, profileImageId: Int): Result<Unit>

    fun getCurrentUser(): Flow<UserData?>

    suspend fun withdrawUser(userId: String): Result<Unit>
}