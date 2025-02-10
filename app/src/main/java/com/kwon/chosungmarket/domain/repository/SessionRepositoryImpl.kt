package com.kwon.chosungmarket.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepositoryImpl {
    suspend fun saveUserId(userId: String): Result<Unit>

    fun getUserId(): Flow<String?>

    suspend fun clearUserId(): Result<Unit>
}