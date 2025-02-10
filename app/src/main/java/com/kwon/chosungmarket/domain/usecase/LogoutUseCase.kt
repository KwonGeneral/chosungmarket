package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl


/**
 * 로그아웃 처리를 담당하는 UseCase
 */
class LogoutUseCase(
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl
) {
    suspend fun invoke(clearAutoLogin: Boolean = false): Result<Unit> {
        try {
            sessionRepositoryImpl.clearUserId()
            if (clearAutoLogin) {
                persistentStorageRepositoryImpl.clearKakaoId()
                persistentStorageRepositoryImpl.setAutoLoginEnabled(false)
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}