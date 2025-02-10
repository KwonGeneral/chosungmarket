package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 세션 관리를 담당하는 Repository 구현체
 * 현재 로그인한 사용자의 ID를 메모리에 캐시하여 관리합니다.
 */
class SessionRepository : SessionRepositoryImpl {
    private val _userIdFlow = MutableStateFlow<String?>(null)

    /** 현재 로그인한 사용자의 ID를 저장합니다. */
    override suspend fun saveUserId(userId: String): Result<Unit> {
        return try {
            _userIdFlow.emit(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 현재 로그인한 사용자의 ID를 관찰합니다. */
    override fun getUserId(): Flow<String?> {
        return _userIdFlow.asStateFlow()
    }

    /** 로그인 세션을 종료합니다. */
    override suspend fun clearUserId(): Result<Unit> {
        return try {
            _userIdFlow.emit(null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}