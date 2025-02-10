package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionRepository : SessionRepositoryImpl {
    private val _userIdFlow = MutableStateFlow<String?>(null)

    override suspend fun saveUserId(userId: String): Result<Unit> {
        return try {
            _userIdFlow.emit(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserId(): Flow<String?> {
        return _userIdFlow.asStateFlow()
    }

    override suspend fun clearUserId(): Result<Unit> {
        return try {
            _userIdFlow.emit(null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}