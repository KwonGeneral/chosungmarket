package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

class AutoLoginUseCase(
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl
) {
    suspend fun invoke(): Result<Boolean> {
        try {
            val isAutoLoginEnabled = persistentStorageRepositoryImpl.observeAutoLoginEnabled().first()
            if (!isAutoLoginEnabled) return Result.success(false)

            val kakaoId = persistentStorageRepositoryImpl.observeKakaoId().first()
                ?: return Result.success(false)

            return userRepositoryImpl.signInWithKakao(kakaoId)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}